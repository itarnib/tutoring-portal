package com.tutoring.portal.model;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTests {

    /**
     * Tests getters and setters.
     */
    @Test
    void testGetterSetter() {
        PojoClass pojoclass = PojoClassFactory.getPojoClass(User.class);
        Validator validator = ValidatorBuilder
                .create()
                .with(new SetterMustExistRule())
                .with(new GetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();
        validator.validate(pojoclass);
    }

    /**
     * Tests case when user has admin role.
     */
    @Test
    void testIsAdmin() {
        Role role = new Role(1, "ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = createUser();
        user.setRoles(roles);

        assertTrue(user.isAdmin());
    }

    /**
     * Tests case when user doesn't have admin role.
     */
    @Test
    void testNotAdmin() {
        Role role = new Role(1, "STUDENT");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = createUser();
        user.setRoles(roles);

        assertFalse(user.isAdmin());
    }

    /**
     * Tests case when user has tutor role.
     */
    @Test
    void testIsTutor() {
        Role role = new Role(1, "TUTOR");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = createUser();
        user.setRoles(roles);

        assertTrue(user.isTutor());
    }

    /**
     * Tests case when user doesn't have tutor role.
     */
    @Test
    void testNotTutor() {
        Role role = new Role(1, "STUDENT");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = createUser();
        user.setRoles(roles);

        assertFalse(user.isTutor());
    }

    /**
     * Helper method for user creation.
     * @return new user
     */
    public User createUser() {
        User user = new User();
        user.setId(1);
        user.setName("Mark");
        user.setSurname("Smith");
        user.setPassword("password");
        user.setEmail("e@example.com");
        return user;
    }
}
