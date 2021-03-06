<%=packageName ? "package ${packageName}\n\n" : ''%>

import org.junit.Test
import org.junit.Before
import grails.test.mixin.domain.DomainClassUnitTestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import grails.test.mixin.TestMixin


@TestMixin([ControllerUnitTestMixin, DomainClassUnitTestMixin])
class ${className}ControllerTests {

    ${className}Controller controller

    @Before
    void setUp() {
        controller = mockController(${className}Controller)
        mockDomain(${className})
    }

    @Test
    void testIndex() {
        controller.index()
        assert "/$propertyName/list" == response.redirectedUrl
    }

    @Test
    void testList() {

        def model = controller.list()

        assert model.${propertyName}InstanceList.size() == 0
        assert model.${propertyName}InstanceTotal == 0

    }

    @Test
    void testCreate() {
       def model = controller.create()

       assert model.${propertyName}Instance != null


    }

    @Test
    void testSave() {
        controller.save()

        assert controller.modelAndView != null
        assert controller.modelAndView.model.${propertyName}Instance != null
        assert controller.modelAndView.viewName == '/${propertyName}/create'

        // TODO: Populate valid properties

        controller.save()

        assert response.redirectedUrl == '/${propertyName}/show/1'
        assert controller.flash.message != null
        assert ${className}.count() == 1
    }


    @Test
    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/${propertyName}/list'


        def ${propertyName} = new ${className}()

        // TODO: populate domain properties

        assert ${propertyName}.save() != null

        params.id = ${propertyName}.id

        def model = controller.show()

        assert model.${propertyName}Instance == ${propertyName}
    }

    @Test
    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/${propertyName}/list'


        def ${propertyName} = new ${className}()

        // TODO: populate valid domain properties

        assert ${propertyName}.save() != null

        params.id = ${propertyName}.id

        def model = controller.edit()

        assert model.${propertyName}Instance == ${propertyName}
    }

    @Test
    void testUpdate() {

        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/${propertyName}/list'

        response.reset()


        def ${propertyName} = new ${className}()

        // TODO: populate valid domain properties

        assert ${propertyName}.save() != null

        // test invalid parameters in update
        params.id = ${propertyName}.id

        controller.update()

        assert controller.modelAndView != null
        assert controller.modelAndView.viewName == "/${propertyName}/edit"
        assert controller.modelAndView.model.${propertyName}Instance != null

        ${propertyName}.clearErrors()

        // TODO: populate valid domain form parameter
        controller.update()

        assert response.redirectedUrl == "/${propertyName}/show/\$${propertyName}.id"
        assert flash.message != null
    }

    @Test
    void testDelete() {
        controller.delete()

        assert flash.message != null
        assert response.redirectedUrl == '/${propertyName}/list'

        response.reset()

        def ${propertyName} = new ${className}()

        // TODO: populate valid domain properties
        assert ${propertyName}.save() != null
        assert ${className}.count() == 1

        params.id = ${propertyName}.id

        controller.delete()

        assert ${className}.count() == 0
        assert ${className}.get(${propertyName}.id) == null
        assert response.redirectedUrl == '/${propertyName}/list'


    }


}