package com.xjt.webapp;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xjt.util.ValidateCode;


/**
 * 验证码
 * @author Administrator
 *
 */
@Controller
public class ValidateCodeController {

	@RequestMapping("validateCode")
	public void createValidateCode(ModelMap modelMap, HttpSession session, HttpServletResponse response) throws IOException{
		ValidateCode validateCode = new ValidateCode(120, 38, 4, 0);
		session.setAttribute("validateCode", validateCode.getCode());
		BufferedImage bufferedImage = validateCode.getBuffImg();
		OutputStream out=response.getOutputStream();
		ImageIO.write(bufferedImage, "jpeg", out);
		out.close();
	}
}
