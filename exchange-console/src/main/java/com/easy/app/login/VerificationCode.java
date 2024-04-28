package com.easy.app.login;



import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/code")
public class VerificationCode extends HttpServlet {
	//private IUserService service;

	private int width = 150;// 定义图片的width
	private int height = 50;// 定义图片的height
	private int codeCount = 4;// 定义图片上显示验证码的个数
	private int xx = 10;
	private int fontHeight = 40;
	private int codeY = 40;
	char[] codeSequence = {'1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private String logoutPath;
    // public void DoCommand() throws Exception {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// 定义图像buffer
    	//HttpServletRequest req =null;
    	//HttpServletResponse resp=null;
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// Graphics2D gd = buffImg.createGraphics();
		// Graphics2D gd = (Graphics2D) buffImg.getGraphics();
		Graphics gd = buffImg.getGraphics();
		// 创建一个随机数生成器类
		Random random = new Random();

		gd.setColor(getRandColor(200, 250));
		gd.fillRect(0, 0, width, height);

		// 创建字体，字体的大小应该根据图片的高度来定。
		// Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
		// 设置字体。
		gd.setFont(getFont(fontHeight));

		// 画边框。
		// gd.setColor(Color.BLACK);
		// gd.drawRect(0, 0, width - 1, height - 1);

		// 随机产生160条干扰线，使图象中的认证码不易被其它程序探测到。
		gd.setColor(getRandColor(160, 200));
		for (int i = 0; i < 160; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(20);
			int yl = random.nextInt(20);
			gd.drawLine(x, y, x + xl, y + yl);
		}

		// randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
		StringBuffer randomCode = new StringBuffer();
		// int red = 0, green = 0, blue = 0;

		// 随机产生codeCount数字的验证码。
		for (int i = 0; i < codeCount; i++) {
			// 得到随机产生的验证码数字。
			String code = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
			// 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
			// red = random.nextInt(255);
			// green = random.nextInt(255);
			// blue = random.nextInt(255);

			// 用随机产生的颜色将验证码绘制到图像中。
			gd.setColor(new Color(30 + random.nextInt(80), 30 + random.nextInt(80), 30 + random.nextInt(80)));
			gd.drawString(code, 30 * i + xx, codeY);

			// 将产生的四个随机数组合在一起。
			randomCode.append(code);
		}
		// 将四位数字的验证码保存到Session中。
		HttpSession session = req.getSession();
		session.setAttribute("code", randomCode.toString());

		gd.dispose();

		// 禁止图像缓存。
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);

		resp.setContentType("image/jpeg");

		ImageIO.write(buffImg, "jpeg", resp.getOutputStream());
		resp.getOutputStream().flush();
		resp.getOutputStream().close();
	}
	/*@RequestMapping(value="/code/verifyCode", method = RequestMethod.GET)
	@ResponseBody*/
	/*public String verifyCode(HttpServletRequest req,@RequestParam String code){
		HttpSession session = req.getSession();
		String sessioncode=String.valueOf(session.getAttribute("code"));
		if(code.equalsIgnoreCase(sessioncode)){
			return "1";
		}
		return "0";
	}*/
	protected Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	/**
	 * 产生随机字体
	 *
	 * @return
	 */
	protected Font getFont(int fontHeight) {
		Random random = new Random();
		Font font[] = new Font[1];
//		font[0] = new Font("Ravie", Font.BOLD, fontHeight);
//		font[1] = new Font("Antique Olive Compact", Font.BOLD, fontHeight);
//		font[2] = new Font("Forte", Font.BOLD, fontHeight);
//		font[0] = new Font("Wide Latin", Font.PLAIN, fontHeight);
//		font[4] = new Font("Gill Sans Ultra Bold", Font.BOLD, fontHeight);
		font[0] = new Font("Fixedsys", Font.BOLD, fontHeight);
		return font[random.nextInt(1)];
	}
	
	public void clearSessionId(HttpServletResponse response){
		String[] paths = logoutPath.split("\\|");
        for (int i = 0; i < paths.length; i++) {
            Cookie jsessionid = new Cookie("JSESSIONID", null);
            jsessionid.setHttpOnly(true);
            jsessionid.setMaxAge(-1);
            jsessionid.setPath("/" + paths[i] + "/");

            Cookie jsessionid2 = new Cookie("JSESSIONID", null);
            jsessionid2.setHttpOnly(true);
            jsessionid2.setMaxAge(-1);
            jsessionid2.setPath("/" + paths[i]);
            response.addCookie(jsessionid);
            response.addCookie(jsessionid2);
        }
	}

}
