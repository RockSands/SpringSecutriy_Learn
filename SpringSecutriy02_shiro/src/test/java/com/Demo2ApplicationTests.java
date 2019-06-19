package com;

import org.apache.catalina.session.StandardManager;
import org.apache.catalina.session.StandardSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.controller.TestController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Demo2ApplicationTests {
	@Autowired
	private TestController testController;

	@Test
	public void contextLoads() {
		StandardManager standardManager = new StandardManager();
		StandardSession standardSession = new StandardSession(standardManager);
		for (int i = 0; i < 10; i++) {
			String code = testController.loginUser("admin", "1234", standardSession);
			System.out.println("==" + i + "==>" + code);
		}

	}

}
