package com.devopsdemo.tutorial.addressbook.backend;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Backend implementation for the address book application, with "detached entities"
 * simulating real-world DAO. Typically, these are provided by Java EE or Spring backend services.
 */
@jakarta.enterprise.context.ApplicationScoped
public class ContactService {

    private static final String[] fnames = { "Peter", "Alice", "John", "Mike", "Olivia",
            "Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Rene", "Lisa",
            "Linda", "Timothy", "Daniel", "Brian", "George", "Scott",
            "Jennifer" };
    private static final String[] lnames = { "Smith", "Johnson", "Williams", "Jones",
            "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor",
            "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin",
            "Thompson", "Young", "King", "Robinson" };

    private static Map<Long, Contact> contacts;
    private static AtomicLong nextId;
    private static ContactService instance;
    
    static {
        contacts = Collections.synchronizedMap(new HashMap<>());
        nextId = new AtomicLong(0);
        instance = new ContactService();
        var r = new Random(0);
        for (int i = 0; i < 100; i++) {
            Contact contact = new Contact();
            contact.setFirstName(fnames[r.nextInt(fnames.length)]);
            contact.setLastName(lnames[r.nextInt(lnames.length)]);
            contact.setEmail(contact.getFirstName().toLowerCase() + "@"
                    + contact.getLastName().toLowerCase() + ".com");
            contact.setPhone("+ 358 555 " + (100 + r.nextInt(900)));
            contact.setBirthDate(LocalDate.of(1930 + r.nextInt(70),
                    r.nextInt(11) + 1, r.nextInt(28) + 1));
            contact.setId(nextId.getAndIncrement());
            contacts.put(contact.getId(), contact);
        }
    }

    private ContactService() {
        // Private constructor
    }

    public static ContactService createDemoService() {
        return instance;
    }

    public List<Contact> findAll(String stringFilter) {
        var filteredContacts = new ArrayList<Contact>();
        for (var contact : contacts.values()) {
            boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
                    || contact.toString().toLowerCase().contains(stringFilter.toLowerCase());
            if (passesFilter) {
                filteredContacts.add(contact);
            }
        }
        filteredContacts.sort(Comparator.comparingLong(Contact::getId).reversed());
        return filteredContacts;
    }

    public long count() {
        return contacts.size();
    }

    public void delete(Contact value) {
        contacts.remove(value.getId());
    }

    public void save(Contact entry) {
        if (entry.getId() == null) {
            entry.setId(nextId.getAndIncrement());
        }
        contacts.put(entry.getId(), entry);
    }
}
