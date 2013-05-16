package learning.junit

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import spock.lang.Specification

/**
 * JUnitの
 * <ul>
 *     <li>{@link Before}
 *     <li>{@link After}
 *     <li>{@link BeforeClass}
 *     <li>{@link AfterClass}
 * </ul>
 * が使える
 */
class JUnitFixtureSpec extends Specification {

    @Before
    void "before1"() {
        println "before1"
    }

    @Before
    void "before2"() {
        println "before2"
    }

    @After
    void "cleanup"() {
        println "after"
    }

    @BeforeClass
    static void "beforeClass"() {
        println "beforeClass"
    }

    @AfterClass
    static void "afterClass"() {
        println "afterClass"
    }

    def "feature1"() {
        expect:
        println "feature1"
    }

    def "feature2"() {
        expect:
        println "feature2"
    }

}
