package org.codehaus.groovy.grails.orm.hibernate

class ValidatePersistentMethodTests extends AbstractGrailsHibernateTests {

    void testClearErrorsBetweenValidations() {
        def personClass = ga.getDomainClass(ValidatePerson.name)
        def person = personClass.newInstance()
        person.age = 999
        assertFalse 'validation should have failed for invalid age', person.validate()
        assertTrue 'person should have had errors because of invalid age', person.hasErrors()
        person.clearErrors()
        assertFalse 'person should not have had errors', person.hasErrors()
        person.age = 9
        assertTrue 'validation should have succeeded', person.validate()
        assertFalse 'person should not have had errors', person.hasErrors()
        person.age = 999
        assertFalse 'validation should have failed for invalid age', person.validate()
        assertTrue 'person should have had errors because of invalid age', person.hasErrors()
    }

    void testToOneCascadingValidation() {
        def bookClass = ga.getDomainClass(ValidateBook.name)
        def authorClass = ga.getDomainClass(ValidateAuthor.name)
        def addressClass = ga.getDomainClass(ValidateAddress.name)

        def book = bookClass.newInstance()

        assert !book.validate()
        assert !book.validate(deepValidate:false)

        book.title = "Foo"

        assert !book.validate()
        assert !book.validate(deepValidate:false)

        def author = authorClass.newInstance()
        book.author = author

        assert book.validate()
        assert book.validate(deepValidate:false)

        author.name = "Bar"

        assert book.validate()
        assert !author.validate()
        assert book.validate(deepValidate:false)

        def address = addressClass.newInstance()

        author.address = address

        assert book.validate()
        assert !author.validate()
        assert book.validate(deepValidate:false)

        address.location = "Foo Bar"

        assert book.validate()
        assert author.validate()
        assert book.validate(deepValidate:false)
    }

    void testToManyCascadingValidation() {
        def bookClass = ga.getDomainClass(ValidateBook.name)
        def authorClass = ga.getDomainClass(ValidateAuthor.name)
        def addressClass = ga.getDomainClass(ValidateAddress.name)

        def author = authorClass.newInstance()

        assert !author.validate()
        author.name = "Foo"

        assert !author.validate()
        assert !author.validate(deepValidate:false)

        def address = addressClass.newInstance()
        author.address = address

        assert !author.validate()
        assert author.validate(deepValidate:false)

        address.location = "Foo Bar"
        assert author.validate()
        assert author.validate(deepValidate:false)

        def book = bookClass.newInstance()

        author.addToBooks(book)
        assert !author.validate()
        assert author.validate(deepValidate:false)

        book.title = "TDGTG"
        assert author.validate()
        assert author.validate(deepValidate:false)
    }

    void testFilteringValidation() {
        // Test validation on a sub-set of the available fields.
        def profileClass = ga.getDomainClass(ValidateProfile.name)
        def profile = profileClass.newInstance()
        profile.email = "This is not an e-mail address"
        assertFalse "Validation should have failed for invalid e-mail address", profile.validate([ "email" ])
        assertEquals 'Profile should have only one error', 1, profile.errors.errorCount
        assertTrue "'email' field should have errors associated with it", profile.errors.hasFieldErrors("email")

        // Now test the behaviour when the object has errors, but none
        // of the fields included in the filter has errors.
        profile.email = "someone@somewhere.org"
        assertTrue "Validation should not have failed for valid e-mail address", profile.validate([ "email" ])
        assertFalse 'Profile should have no errors', profile.hasErrors()

        // Finally check that without the filtering, the target would have errors.
        assertFalse "Validation should have failed", profile.validate()
        assertTrue 'Profile should have errors', profile.hasErrors()
    }

    void testValidationAfterBindingErrors() {
        def teamClass = ga.getDomainClass(ValidateTeam.name)
        def team = teamClass.newInstance()
        team.properties = [homePage: 'invalidurl']
        assertFalse 'validation should have failed', team.validate()
        assertEquals 'wrong number of errors found', 2, team.errors.errorCount
        assertEquals 'wrong number of homePage errors found', 1, team.errors.getFieldErrors('homePage')?.size()
        def homePageError = team.errors.getFieldError('homePage')
        assertTrue 'did not find typeMismatch error', 'typeMismatch' in homePageError.codes

        team.homePage = new URL('http://grails.org')
        assertFalse 'validation should have failed', team.validate()
        assertEquals 'wrong number of errors found', 1, team.errors.errorCount
        assertEquals 'wrong number of homePage errors found', 0, team.errors.getFieldErrors('homePage')?.size()
    }

    protected getDomainClasses() {
        [ValidateTeam, ValidatePerson, ValidateProfile, ValidateBook, ValidateAuthor, ValidateAddress]
    }
}

class ValidateTeam {
    Long id
    Long version
    String name
    URL homePage
}

class ValidatePerson {
    Long id
    Long version
    Integer age
    static constraints = {
      age(max:99)
    }
}
class ValidateProfile {
    Long id
    Long version
    String firstName
    String lastName
    String email
    Date dateOfBirth

    static constraints = {
        firstName(nullable: false, blank: false)
        lastName(nullable: false, blank: false)
        email(nullable: true, blank: true, email: true)
        dateOfBirth(nullable: true)
    }
}
class ValidateBook {
    Long id
    Long version
    String title
    ValidateAuthor author
    static belongsTo = ValidateAuthor
    static constraints = {
       title(blank:false, size:1..255)
       author(nullable:false)
    }
}
class ValidateAuthor {
   Long id
   Long version
   String name
   ValidateAddress address
   Set books = new HashSet()
   static hasMany = [books:ValidateBook]
   static constraints = {
        address(nullable:false)
        name(size:1..255, blank:false)
   }
}
class ValidateAddress {
    Long id
    Long version
    ValidateAuthor author
    String location
    static belongsTo = ValidateAuthor
    static constraints = {
       author(nullable:false)
       location(blank:false)
    }
}
