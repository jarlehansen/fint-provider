package no.fint.provider.testmode

class TestModeConditional {

    static Boolean integrationTest() {
        def value = System.getenv('SPRING_PROFILES_ACTIVE') ?: System.getProperty('spring.profiles.active')
        return (value?.toLowerCase() == 'integration')
    }

}
