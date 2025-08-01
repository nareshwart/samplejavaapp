package com.devopsdemo.tutorial.addressbook;

import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.ServletException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServlet;
import com.devopsdemo.tutorial.addressbook.backend.Contact;
import com.devopsdemo.tutorial.addressbook.backend.ContactService;

@Route("/")
public class AddressbookUI extends VerticalLayout {

    protected TextField filter;
    protected Grid<Contact> contactList;
    protected Button newContact;
    protected ContactForm contactForm;
    private final ContactService service;
    
    @jakarta.inject.Inject
    public AddressbookUI(ContactService service) {
        this.service = service;
        
        // Initialize UI components
        filter = new TextField("Filter contacts...");
        contactList = new Grid<>(Contact.class);
        newContact = new Button("New contact");
        contactForm = new ContactForm();
    }
    
    public AddressbookUI() {
        this(ContactService.createDemoService());

        contactForm.setListener(new ContactForm.ContactFormListener() {
            @Override
            public void onSave() {
                refreshContacts();
            }
            @Override
            public void onCancel() {
                contactList.deselectAll();
            }
        });
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        // Configure the new contact button
        newContact.addClickListener(e -> {
            contactForm.edit(new Contact());
            contactForm.setVisible(true);
        });

        // Configure the filter
        filter.setPlaceholder("Filter contacts...");
        filter.addValueChangeListener(e -> refreshContacts(e.getValue()));

        // Configure the grid
        contactList.setColumns("firstName", "lastName", "email", "phone");
        contactList.setSelectionMode(Grid.SelectionMode.SINGLE);
        contactList.asSingleSelect().addValueChangeListener(e -> {
            Contact selected = e.getValue();
            contactForm.setVisible(selected != null);
            if (selected != null) {
                contactForm.edit(selected);
            }
        });

        // Initial load of contacts
        refreshContacts();
    }

    private void buildLayout() {
        HorizontalLayout actions = new HorizontalLayout(filter, newContact);
        actions.setWidthFull();
        filter.setWidthFull();
        actions.expand(filter);

        VerticalLayout left = new VerticalLayout(actions, contactList);
        left.setSizeFull();
        contactList.setSizeFull();
        left.expand(contactList);

        HorizontalLayout mainLayout = new HorizontalLayout(left, contactForm);
        mainLayout.setSizeFull();
        mainLayout.expand(left);

        add(mainLayout);
    }

    private void refreshContacts() {
        refreshContacts(filter.getValue());
    }

    private void refreshContacts(String stringFilter) {
        contactList.setItems(service.findAll(stringFilter));
        contactForm.setVisible(false);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    public static class MyUIServlet extends VaadinServlet {
        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().setClassLoader(getClass().getClassLoader());
        }
    }
}
