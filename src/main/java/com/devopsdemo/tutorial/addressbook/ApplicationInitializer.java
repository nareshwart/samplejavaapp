package com.devopsdemo.tutorial.addressbook;

import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;
import com.devopsdemo.tutorial.addressbook.backend.ContactService;

@WebListener
public class ApplicationInitializer implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize the ContactService at application startup
        ContactService.createDemoService();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}
