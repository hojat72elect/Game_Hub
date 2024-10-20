package ca.hojat.gamehub.core.data.api.gamespot

import ca.hojat.gamehub.core.data.api.gamespot.common.serialization.Gamespot
import ca.hojat.gamehub.core.data.api.gamespot.common.serialization.GamespotFieldsSerializer
import ca.hojat.gamehub.core.data.api.gamespot.common.serialization.GamespotFieldsSerializerImpl
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class GamespotFieldsSerializerImplTest {

    private lateinit var sut: GamespotFieldsSerializer

    @Before
    fun setup() {
        sut = GamespotFieldsSerializerImpl()
    }

    @Test
    fun `Serializes simple entity successfully`() {
        data class Entity(
            @Gamespot("field1")
            val field1: Int,
            @Gamespot("field2")
            val field2: String,
            @Gamespot("field3")
            val field3: Double,
            @Gamespot("field4")
            val field4: Float,
            @Gamespot("field5")
            val field5: String,
            @Gamespot("field6")
            val field6: Float
        )

        assertThat(sut.serializeFields(Entity::class.java))
            .isEqualTo("field1,field2,field3,field4,field5,field6")
    }

    @Test
    fun `Serializes entity with no annotated fields successfully`() {
        data class Entity(
            val field1: Int,
            val field2: String,
            val field3: Double
        )

        assertThat(sut.serializeFields(Entity::class.java)).isEmpty()
    }

    @Test
    fun `Throws exception if name of field is empty`() {
        data class Entity(
            @Gamespot("")
            val field1: Int
        )

        assertThrows(IllegalArgumentException::class.java) {
            sut.serializeFields(Entity::class.java)
        }
    }

    @Test
    fun `Throws exception if name of field is blank`() {
        data class Entity(
            @Gamespot("   ")
            val field1: Int
        )

        assertThrows(IllegalArgumentException::class.java) {
            sut.serializeFields(Entity::class.java)
        }
    }
}
