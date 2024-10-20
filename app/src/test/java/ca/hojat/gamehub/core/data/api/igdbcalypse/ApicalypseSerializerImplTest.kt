package ca.hojat.gamehub.core.data.api.igdbcalypse

import ca.hojat.gamehub.core.data.api.igdbcalypse.serialization.ApicalypseSerializerImpl
import ca.hojat.gamehub.core.data.api.igdbcalypse.serialization.annotations.Apicalypse
import ca.hojat.gamehub.core.data.api.igdbcalypse.serialization.annotations.ApicalypseClass
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class ApicalypseSerializerImplTest {

    private lateinit var sut: ApicalypseSerializerImpl

    @Before
    fun setup() {
        sut = ApicalypseSerializerImpl()
    }

    @Test
    fun `Serializes simple entity successfully`() {
        @ApicalypseClass
        data class Entity(
            @Apicalypse("field1")
            val field1: Int,
            @Apicalypse("field2")
            val field2: String,
            @Apicalypse("field3")
            val field3: Double,
            @Apicalypse("field4")
            val field4: Float,
            @Apicalypse("field5")
            val field5: String,
            @Apicalypse("field6")
            val field6: Float
        )

        assertThat(sut.serialize(Entity::class.java))
            .isEqualTo("field1, field2, field3, field4, field5, field6")
    }

    @Test
    fun `Throws exception if entity does not have @ApicalypseClass annotation`() {
        data class Entity(
            val field1: Int,
            val field2: Float
        )

        assertThrows(IllegalArgumentException::class.java) {
            sut.serialize(Entity::class.java)
        }
    }

    @Test
    fun `Throws exception if name of field is empty`() {
        @ApicalypseClass
        data class Entity(
            @Apicalypse("")
            val field1: Int
        )

        assertThrows(IllegalArgumentException::class.java) {
            sut.serialize(Entity::class.java)
        }
    }

    @Test
    fun `Throws exception if name of field is blank`() {
        @ApicalypseClass
        data class Entity(
            @Apicalypse("   ")
            val field1: Int
        )

        assertThrows(IllegalArgumentException::class.java) {
            sut.serialize(Entity::class.java)
        }
    }

    @Test
    fun `Does not serialize fields that are not annotated with @Apicalypse annotation`() {
        @ApicalypseClass
        data class Entity(
            @Apicalypse("field1")
            val field1: Int,
            val field2: Float
        )

        assertThat(sut.serialize(Entity::class.java)).isEqualTo("field1")
    }

    @Test
    fun `Serializes parent entity that contains child entities successfully`() {
        @ApicalypseClass
        data class Child1(
            @Apicalypse("field1")
            val field1: Int
        )

        @ApicalypseClass
        data class Child2(
            @Apicalypse("field1")
            val field1: Int,
            @Apicalypse("field2")
            val field2: String
        )

        @ApicalypseClass
        data class Child3(
            @Apicalypse("field1")
            val field1: Int,
            @Apicalypse("field2")
            val field2: String,
            @Apicalypse("field3")
            val field3: Double
        )

        @ApicalypseClass
        data class Parent(
            @Apicalypse("parent")
            val parent: Int,
            @Apicalypse("child1")
            val child1: Child1,
            @Apicalypse("child2")
            val child2: Child2,
            @Apicalypse("child3")
            val child3: Child3
        )

        assertThat(sut.serialize(Parent::class.java))
            .isEqualTo(
                "parent, child1.field1, child2.field1, child2.field2, child3.field1, child3.field2, " +
                        "child3.field3"
            )
    }

    @Test
    @Suppress("LongMethod")
    fun `Serializes parent entity with deeply nested child entities successfully`() {
        @ApicalypseClass
        data class Grandchild1(
            @Apicalypse("field1")
            val field1: Int
        )

        @ApicalypseClass
        data class Grandchild2(
            @Apicalypse("field1")
            val field1: String,
            @Apicalypse("field2")
            val field2: String
        )

        @ApicalypseClass
        data class Grandchild3(
            @Apicalypse("field1")
            val field1: String,
            @Apicalypse("field2")
            val field2: String,
            @Apicalypse("field3")
            val field3: String
        )

        @ApicalypseClass
        data class Child1(
            @Apicalypse("kid1")
            val child1: Grandchild1
        )

        @ApicalypseClass
        data class Child2(
            @Apicalypse("kid1")
            val child1: Grandchild1,
            @Apicalypse("kid2")
            val child2: Grandchild2
        )

        @ApicalypseClass
        data class Child3(
            @Apicalypse("kid1")
            val child1: Grandchild1,
            @Apicalypse("kid2")
            val child2: Grandchild2,
            @Apicalypse("kid3")
            val child3: Grandchild3
        )

        @ApicalypseClass
        data class Parent(
            @Apicalypse("parent")
            val parent: Int,
            @Apicalypse("child1")
            val child1: Child1,
            @Apicalypse("child2")
            val child2: Child2,
            @Apicalypse("child3")
            val child3: Child3
        )

        assertThat(sut.serialize(Parent::class.java))
            .isEqualTo(
                "parent, child1.kid1.field1, child2.kid1.field1, child2.kid2.field1, child2.kid2.field2, " +
                        "child3.kid1.field1, child3.kid2.field1, child3.kid2.field2, child3.kid3.field1, " +
                        "child3.kid3.field2, child3.kid3.field3"
            )
    }

    @Test
    fun `Serializes entity with list collection type`() {
        @ApicalypseClass
        data class Entity(
            @Apicalypse("field1")
            val field1: Int,
            @Apicalypse("field2")
            val field2: List<String>
        )

        assertThat(sut.serialize(Entity::class.java)).isEqualTo("field1, field2")
    }

    @Test
    fun `Serializes entity with set collection type`() {
        @ApicalypseClass
        data class Entity(
            @Apicalypse("field1")
            val field1: Int,
            @Apicalypse("field2")
            val field2: Set<String>
        )

        assertThat(sut.serialize(Entity::class.java)).isEqualTo("field1, field2")
    }

    @Test
    fun `Throws exception when serializing non collection generic type`() {
        @ApicalypseClass
        data class Entity(
            @Apicalypse("field1")
            val field1: Int,
            @Apicalypse("field2")
            val field2: Map<String, String>
        )

        assertThrows(IllegalStateException::class.java) {
            sut.serialize(Entity::class.java)
        }
    }
}
