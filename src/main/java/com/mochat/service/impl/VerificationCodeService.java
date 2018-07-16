package com.mochat.service.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mochat.redis.CustomRedisClusters;
import com.util.UUIDGenerator;

import redis.clients.jedis.JedisCluster;

@Service
public class VerificationCodeService {

	@Autowired
	private CustomRedisClusters customRedisCluster;
	
	 // 渲染随机背景颜色
    private Color getRandColor(int fc,int bc){
        Random random = new Random();
        if(fc>255) fc=255;
        if(bc>255) bc=255;
        int r=fc+random.nextInt(bc-fc);
        int g=fc+random.nextInt(bc-fc);
        int b=fc+random.nextInt(bc-fc);
        return new Color(r,g,b);
    }
     
     //渲染固定背景颜色
    private Color getBgColor(){
       return new Color(200,200,200);
    }
     
     
    private Map<String, Object>  drawImg(){
        return drawImg(false);
    }
     
    /**
     * 画验证码图形
     * @param isDrawLine
     * @return
     */
    private Map<String, Object>  drawImg(boolean isDrawLine){
    	Map<String, Object> map = new HashMap<String, Object>();
        // 在内存中创建图象
        int width = 60, height = 33;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文
        Graphics g = image.getGraphics();
        Random random = new Random();
	    g.fillRect(0, 0, width, height);

	    // 创建字体，字体的大小应该根据图片的高度来定。
	    Font font = new Font("Fixedsys", Font.BOLD, 18);
	    // 设置字体。
	    g.setFont(font);

	    // 画边框。
	    g.setColor(Color.BLACK);
        // 画边框
        //g.setColor(new Color());
        //g.drawRect(0,0,width-1,height-1);
        /**随机产生155条干扰线，使图象中的认证码不易被其它程序探测到*/
        if(isDrawLine){
            g.setColor(getRandColor(160,200));
            for (int i=0;i<155;i++){
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int xl = random.nextInt(12);
                int yl = random.nextInt(12);
                g.drawLine(x,y,x+xl,y+yl);
            }
        }
        // 取随机产生的认证码(4位数字和字母混合)
        String sRand="";
        String verCode="";
        char[] seds = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};
        for (int i=0;i<4;i++){
            int index = random.nextInt(seds.length);
            char cc = seds[index];
            if((i+1)%2==0){
                verCode+=cc;
                g.setColor(new Color(255,0,0));
            }else{
                g.setColor(new Color(0,0,0));
            }
            sRand+=cc;
            // 将认证码随机打印不同的颜色显示出来
            //g.setColor(new Color(20+random.nextInt(110),20+random.nextInt(110),20+random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.drawString(cc+"",13*i+6,20);
        }
        // 图象生效
        g.dispose();
        map.put("sRand",sRand);
        map.put("verCode", verCode);
        map.put("image", image);
        return map;
    }
     
    public  Map<String, Object> encodeBase64ImgCode()throws ServletException, IOException {
    	Map<String, Object> map = drawImg();
        BufferedImage codeImg = (BufferedImage)map.get("image");
        String sRand = UUIDGenerator.generate();
        JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
        jedisCluster.set(sRand, String.valueOf(map.get("sRand")));
        jedisCluster.expire(sRand, 120);
        map.put("sRand", sRand);
        /*// 将认证码存入SESSION
        request.getSession().setAttribute("rand",sRand);*/
        // 将认证码存入redis
        // RedisUtil.saveValue(redis, uuid.getBytes(),verCode.getBytes(), 60 * 5 * 1);
        ByteArrayOutputStream out = null;
        String imgString = null;
        try {
        	out = new ByteArrayOutputStream();
            boolean flag = ImageIO.write(codeImg, "JPEG", out);
            byte[] b = out.toByteArray();
             imgString = Base64.encodeBase64String(b);
		} finally {
			// TODO: handle finally clause
			org.apache.commons.io.IOUtils.closeQuietly(out);
		}
        map.put("image", "data:image/JPEG;base64," + imgString);
        return map;
    }
     
}
