package com.paramResolver.method;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.util.WebUtils;

import com.paramResolver.FormModel;
import com.util.MapWapper;

public class FormModelMethodArgumentResolver implements
		HandlerMethodArgumentResolver {
	
	public static final Pattern pattern = Pattern.compile("[a-zA-Z]+\\s[a-zA-Z]+\\s\\d{2}\\s{1}\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\s[a-zA-Z]+\\s\\d+");
	public FormModelMethodArgumentResolver() {
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (parameter.hasParameterAnnotation(FormModel.class)) {
			return true;
		}
		return false;
	}

	/**
	 * Resolve the argument from the model or if not found instantiate it with
	 * its default if it is available. The model attribute is then populated
	 * with request values via data binding and optionally validated if
	 * {@code @java.validation.Valid} is present on the argument.
	 * 
	 * @throws BindException
	 *             if data binding and validation result in an error and the
	 *             next method parameter is not of type {@link Errors}.
	 * @throws Exception
	 *             if WebDataBinder initialization fails.
	 */
	public final Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest request,
			WebDataBinderFactory binderFactory) throws Exception {
		String name = parameter.getParameterAnnotation(FormModel.class).value();

		Object target = (mavContainer.containsAttribute(name)) ? mavContainer
				.getModel().get(name) : createAttribute(name, parameter,
				binderFactory, request);

		WebDataBinder binder = binderFactory
				.createBinder(request, target, name);
		Object tempObject = bindRequestParameters(mavContainer, binderFactory,
				binder, request, parameter);
		return tempObject;
	}

	/**
	 * Extension point to create the model attribute if not found in the model.
	 * The default implementation uses the default constructor.
	 * 
	 * @param attributeName
	 *            the name of the attribute, never {@code null}
	 * @param parameter
	 *            the method parameter
	 * @param binderFactory
	 *            for creating WebDataBinder instance
	 * @param request
	 *            the current request
	 * @return the created model attribute, never {@code null}
	 */
	protected Object createAttribute(String attributeName,
			MethodParameter parameter, WebDataBinderFactory binderFactory,
			NativeWebRequest request) throws Exception {

		String value = getRequestValueForAttribute(attributeName, request);

		if (value != null) {
			Object attribute = createAttributeFromRequestValue(value,
					attributeName, parameter, binderFactory, request);
			if (attribute != null) {
				return attribute;
			}
		}
		Class<?> parameterType = parameter.getParameterType();
		if (parameterType.isArray()
				|| List.class.isAssignableFrom(parameterType)) {
			return ArrayList.class.newInstance();
		}
		if (Set.class.isAssignableFrom(parameterType)) {
			return HashSet.class.newInstance();
		}

		if (MapWapper.class.isAssignableFrom(parameterType)) {
			return MapWapper.class.newInstance();
		}

		return BeanUtils.instantiateClass(parameter.getParameterType());
	}

	/**
	 * Obtain a value from the request that may be used to instantiate the model
	 * attribute through type conversion from String to the target type.
	 * <p>
	 * The default implementation looks for the attribute name to match a URI
	 * variable first and then a request parameter.
	 * 
	 * @param attributeName
	 *            the model attribute name
	 * @param request
	 *            the current request
	 * @return the request value to try to convert or {@code null}
	 */
	protected String getRequestValueForAttribute(String attributeName,
			NativeWebRequest request) {
		Map<String, String> variables = getUriTemplateVariables(request);
		if (StringUtils.hasText(variables.get(attributeName))) {
			return variables.get(attributeName);
		} else if (StringUtils.hasText(request.getParameter(attributeName))) {
			return request.getParameter(attributeName);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	protected final Map<String, String> getUriTemplateVariables(
			NativeWebRequest request) {
		Map<String, String> variables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
						RequestAttributes.SCOPE_REQUEST);
		return (variables != null) ? variables : Collections
				.<String, String> emptyMap();
	}

	/**
	 * Create a model attribute from a String request value (e.g. URI template
	 * variable, request parameter) using type conversion.
	 * <p>
	 * The default implementation converts only if there a registered
	 * {@link Converter} that can perform the conversion.
	 * 
	 * @param sourceValue
	 *            the source value to create the model attribute from
	 * @param attributeName
	 *            the name of the attribute, never {@code null}
	 * @param parameter
	 *            the method parameter
	 * @param binderFactory
	 *            for creating WebDataBinder instance
	 * @param request
	 *            the current request
	 * @return the created model attribute, or {@code null}
	 * @throws Exception
	 */
	protected Object createAttributeFromRequestValue(String sourceValue,
			String attributeName, MethodParameter parameter,
			WebDataBinderFactory binderFactory, NativeWebRequest request)
			throws Exception {
		DataBinder binder = binderFactory.createBinder(request, null,
				attributeName);
		ConversionService conversionService = binder.getConversionService();
		if (conversionService != null) {
			TypeDescriptor source = TypeDescriptor.valueOf(String.class);
			TypeDescriptor target = new TypeDescriptor(parameter);
			if (conversionService.canConvert(source, target)) {
				return binder.convertIfNecessary(sourceValue,
						parameter.getParameterType(), parameter);
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Downcast {@link WebDataBinder} to {@link ServletRequestDataBinder} before
	 * binding.
	 * 
	 * @throws Exception
	 * @see ServletRequestDataBinderFactory
	 */
	protected Object bindRequestParameters(ModelAndViewContainer mavContainer,
			WebDataBinderFactory binderFactory, WebDataBinder binder,
			NativeWebRequest request, MethodParameter parameter)
			throws Exception {

		Class<?> targetType = binder.getTarget().getClass();
		HttpServletRequest servletRequest = request
				.getNativeRequest(HttpServletRequest.class);// prepareServletRequest(binder.getTarget(),
															// request,
															// parameter);
		WebDataBinder simpleBinder = binderFactory.createBinder(request, null,
				null);
		Type type = parameter.getGenericParameterType();
		Matcher matcher = null;
		if (Collection.class.isAssignableFrom(targetType)) {// bind collection

			Collection target = (Collection) binder.getTarget();
			Class<?> componentType = Object.class;

			if (type instanceof ParameterizedType) {
				componentType = (Class<?>) ((ParameterizedType) type)
						.getActualTypeArguments()[0];
			}
			if (parameter.getParameterType().isArray()) {
				componentType = parameter.getParameterType().getComponentType();
			}
			HashSet<Map<String, Object>> tempValue = new HashSet<Map<String, Object>>();
			for (Object key : servletRequest.getParameterMap().keySet()) {
				String prefixName = getPrefixName((String) key);
				 matcher = pattern.matcher(key.toString());
				if(matcher.find()) {
					continue;
				}
				Map<String, Object> paramValues = WebUtils
						.getParametersStartingWith(servletRequest, prefixName);
				
				tempValue.add(paramValues);
			}
			// target.addAll(Arrays.asList(tempValue.toArray()));
			Iterator<Map<String, Object>> iterator = tempValue.iterator();
			Map<String, Object> object = null;
			while (iterator.hasNext()) {
				object = (Map<String, Object>) iterator.next();
				Map<String, String> mapTempMap = convertMap(object);
				Object temp = convertMap(componentType, mapTempMap);
				if(temp instanceof Collection){
					target.addAll((Collection)temp);
				}else{
					target.add(temp);
				}
			}
			return target;
		} else if (MapWapper.class.isAssignableFrom(targetType)) {
			Map target = new HashMap();
			((MapWapper) binder.getTarget()).setInnerMap(target);
			MapWapper mapWapper = new MapWapper<>();
			for (Object key : servletRequest.getParameterMap().keySet()) {
				String prefixName = getPrefixName((String) key);
				matcher = pattern.matcher(key.toString());
				if(matcher.find()) {
					continue;
				}
				Map<String, Object> paramValues = WebUtils
						.getParametersStartingWith(servletRequest, prefixName);

				target = convertMap(paramValues);
			}
			mapWapper.setInnerMap(target);
			return mapWapper;
		} else {
			if (type == targetType) {
				String contentType = servletRequest.getContentType();
				MultipartHttpServletRequest multipartHttpServletRequest = null;
				if (contentType != null
						&& contentType.toLowerCase().startsWith("multipar")) {
					multipartHttpServletRequest = convertRequest(servletRequest);
					for (Object key : multipartHttpServletRequest
							.getParameterMap().keySet()) {
						Introspector.getBeanInfo(targetType).getClass();
						String prefixName = getPrefixName((String) key);
						matcher = pattern.matcher(key.toString());
						if(matcher.find()) {
							continue;
						}
						Map<String, Object> paramValues = WebUtils
								.getParametersStartingWith(
										multipartHttpServletRequest, prefixName);
						return convertMap(targetType, convertMap(paramValues));
					}
				}
				for (Object key : servletRequest.getParameterMap().keySet()) {
					Introspector.getBeanInfo(targetType).getClass();
					String prefixName = getPrefixName((String) key);
					Map<String, Object> paramValues = WebUtils
							.getParametersStartingWith(servletRequest,
									prefixName);
					return convertMap(targetType, convertMap(paramValues));
				}
			}
			throw new Exception("无法转换参数");
		}
	}

	private MultipartHttpServletRequest convertRequest(
			HttpServletRequest request) {
		MultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		MultipartHttpServletRequest multipartHttpServletRequest = multipartResolver
				.resolveMultipart(request);
		return multipartHttpServletRequest;
	}

	private Map<String, String> convertMap(Map<String, ?> map) {
		Map<String, String> mapTemp = new HashMap<String, String>();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			String value = entry.getValue().toString();
			String key = entry.getKey().toString().replace("[", "")
					.replace("]", "");
			mapTemp.put(key, value);
		}
		return mapTemp;
	}

	private Object getMapKey(String prefixName) {
		String key = prefixName;
		if (key.startsWith("['")) {
			key = key.replaceAll("\\[\'", "").replaceAll("\'\\]", "");
		}
		if (key.startsWith("[\"")) {
			key = key.replaceAll("\\[\"", "").replaceAll("\"\\]", "");
		}
		if (key.endsWith(".")) {
			key = key.substring(0, key.length() - 1);
		}
		return key;
	}

	private boolean isSimpleComponent(String prefixName) {
		return !prefixName.endsWith(".");
	}

	private String getPrefixName(String name) {

		int begin = 0;

		int end = name.indexOf("]") + 1;

		if (name.indexOf("].") >= 0) {
			end = end + 1;
		}

		return name.substring(begin, end);
	}

	private ServletRequest prepareServletRequest(Object target,
			NativeWebRequest request, MethodParameter parameter) {

		String modelPrefixName = parameter.getParameterAnnotation(
				FormModel.class).value();

		HttpServletRequest nativeRequest = (HttpServletRequest) request
				.getNativeRequest();
		MultipartRequest multipartRequest = WebUtils.getNativeRequest(
				nativeRequest, MultipartRequest.class);

		MockHttpServletRequest mockRequest = null;
		if (multipartRequest != null) {
			MockMultipartHttpServletRequest mockMultipartRequest = new MockMultipartHttpServletRequest();
			mockMultipartRequest.getMultiFileMap().putAll(
					multipartRequest.getMultiFileMap());
		} else {
			mockRequest = new MockHttpServletRequest();
		}

		for (Entry<String, String> entry : getUriTemplateVariables(request)
				.entrySet()) {
			String parameterName = entry.getKey();
			String value = entry.getValue();
			if (isFormModelAttribute(parameterName, modelPrefixName)) {
				mockRequest.setParameter(
						getNewParameterName(parameterName, modelPrefixName),
						value);
			}
		}

		for (Object parameterEntry : nativeRequest.getParameterMap().entrySet()) {
			Entry<String, String[]> entry = (Entry<String, String[]>) parameterEntry;
			String parameterName = entry.getKey();
			String[] value = entry.getValue();
			if (isFormModelAttribute(parameterName, modelPrefixName)) {
				mockRequest.setParameter(
						getNewParameterName(parameterName, modelPrefixName),
						value);
			}
		}

		return mockRequest;
	}

	private String getNewParameterName(String parameterName,
			String modelPrefixName) {
		int modelPrefixNameLength = modelPrefixName.length();

		if (parameterName.charAt(modelPrefixNameLength) == '.') {
			return parameterName.substring(modelPrefixNameLength + 1);
		}

		if (parameterName.charAt(modelPrefixNameLength) == '[') {
			return parameterName.substring(modelPrefixNameLength);
		}
		throw new IllegalArgumentException(
				"illegal request parameter, can not binding to @FormBean("
						+ modelPrefixName + ")");
	}

	private boolean isFormModelAttribute(String parameterName,
			String modelPrefixName) {
		int modelPrefixNameLength = modelPrefixName.length();

		if (parameterName.length() == modelPrefixNameLength) {
			return false;
		}

		if (!parameterName.startsWith(modelPrefixName)) {
			return false;
		}

		char ch = (char) parameterName.charAt(modelPrefixNameLength);

		if (ch == '.' || ch == '[') {
			return true;
		}

		return false;
	}

	protected void validateComponent(WebDataBinder binder,
			MethodParameter parameter) throws BindException {

		boolean validateParameter = validateParameter(parameter);
		Annotation[] annotations = binder.getTarget().getClass()
				.getAnnotations();
		for (Annotation annot : annotations) {
			if (annot.annotationType().getSimpleName().startsWith("Valid")
					&& validateParameter) {
				Object hints = AnnotationUtils.getValue(annot);
				binder.validate(hints instanceof Object[] ? (Object[]) hints
						: new Object[] { hints });
			}
		}

		if (binder.getBindingResult().hasErrors()) {
			if (isBindExceptionRequired(binder, parameter)) {
				throw new BindException(binder.getBindingResult());
			}
		}
	}

	private boolean validateParameter(MethodParameter parameter) {
		Annotation[] annotations = parameter.getParameterAnnotations();
		for (Annotation annot : annotations) {
			if (annot.annotationType().getSimpleName().startsWith("Valid")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Validate the model attribute if applicable.
	 * <p>
	 * The default implementation checks for {@code @javax.validation.Valid}.
	 * 
	 * @param binder
	 *            the DataBinder to be used
	 * @param parameter
	 *            the method parameter
	 */
	protected void validateIfApplicable(WebDataBinder binder,
			MethodParameter parameter) {
		Annotation[] annotations = parameter.getParameterAnnotations();
		for (Annotation annot : annotations) {
			if (annot.annotationType().getSimpleName().startsWith("Valid")) {
				Object hints = AnnotationUtils.getValue(annot);
				binder.validate(hints instanceof Object[] ? (Object[]) hints
						: new Object[] { hints });
			}
		}
	}

	/**
	 * Whether to raise a {@link BindException} on bind or validation errors.
	 * The default implementation returns {@code true} if the next method
	 * argument is not of type {@link Errors}.
	 * 
	 * @param binder
	 *            the data binder used to perform data binding
	 * @param parameter
	 *            the method argument
	 */
	protected boolean isBindExceptionRequired(WebDataBinder binder,
			MethodParameter parameter) {
		int i = parameter.getParameterIndex();
		Class<?>[] paramTypes = parameter.getMethod().getParameterTypes();
		boolean hasBindingResult = (paramTypes.length > (i + 1) && Errors.class
				.isAssignableFrom(paramTypes[i + 1]));

		return !hasBindingResult;
	}

	// map 转为对象
	private Object convertMap(Class type, Map map)
			throws IntrospectionException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
		Object obj = type.newInstance(); // 创建 JavaBean 对象
		Iterator<Map.Entry<String, Object>> mapIterator = map.entrySet()
				.iterator();
		if (String.class == type) {
			
			if(map.size() == 1){
				while (mapIterator.hasNext()) {
					Map.Entry<String, Object> tempEntry = mapIterator.next();
				    return tempEntry.getValue();
				}
			}else if(map.size()>1){
				Collection targetCollection = new ArrayList<>();
				while (mapIterator.hasNext()) {
					Map.Entry<String, Object> tempEntry = mapIterator.next();
					targetCollection.add(tempEntry.getValue());
				}
				return targetCollection;
			}
		}

		// 给 JavaBean 对象的属性赋值
		PropertyDescriptor[] propertyDescriptors = beanInfo
				.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!Collection.class
					.isAssignableFrom(descriptor.getPropertyType())
					&& map.containsKey(propertyName)) {
				// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
				Object value = map.get(propertyName);

				Object[] args = new Object[1];
				args[0] = value;

				descriptor.getWriteMethod().invoke(obj, args);
			}
		}
		return obj;
	}
}
