package ca.hojat.gamehub.core.data.api.igdbcalypse

import ca.hojat.gamehub.core.data.api.igdbcalypse.querybuilder.ApicalypseQueryBuilderImpl
import ca.hojat.gamehub.core.data.api.igdbcalypse.querybuilder.whereclause.WhereClauseBuilderFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ApicalypseQueryBuilderImplTest {

    private lateinit var sut: ApicalypseQueryBuilderImpl

    @Before
    fun setup() {
        sut = ApicalypseQueryBuilderImpl(WhereClauseBuilderFactory)
    }

    @Test
    fun `Builds search query successfully`() {
        val query = sut.search("Shadow of the Colossus")
            .select("id, title")
            .offset(10)
            .limit(20)
            .sortDesc("id")
            .build()

        assertThat(query)
            .isEqualTo("search \"Shadow of the Colossus\";fields id, title;offset 10;limit 20;sort id desc;")
    }

    @Test
    fun `Builds select query successfully`() {
        val query = sut.select("id, title")
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select all query successfully`() {
        val query = sut.selectAll()
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields *;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select all query with excluded fields successfully`() {
        val query = sut.selectAll()
            .exclude("date, likes")
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields *;exclude date, likes;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some condition is true`() {
        val query = sut.select("id, title, isLiked")
            .where { "isLiked".isTrue }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, isLiked;where isLiked = true;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some condition is false`() {
        val query = sut.select("id, title, isLiked")
            .where { "isLiked".isFalse }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, isLiked;where isLiked = false;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some condition is null`() {
        val query = sut.select("id, title, date")
            .where { "date".isNull }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, date;where date = null;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some condition is not null`() {
        val query = sut.select("id, title, date")
            .where { "date".isNotNull }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, date;where date != null;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field is equal to value`() {
        val query = sut.select("id, title, date")
            .where { "date".isEqual("2020-03-01") }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, date;where date = 2020-03-01;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field is not equal to value`() {
        val query = sut.select("id, title, date")
            .where { "date".isNotEqual("2020-03-01") }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, date;where date != 2020-03-01;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field is larger than value`() {
        val query = sut.select("id, title, likes")
            .where { "likes".isLargerThan("20") }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, likes;where likes > 20;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field is larger than or equal to value`() {
        val query = sut.select("id, title, likes")
            .where { "likes".isLargerThanOrEqualTo("20") }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, likes;where likes >= 20;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field is smaller than value`() {
        val query = sut.select("id, title, likes")
            .where { "likes".isSmallerThan("20") }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, likes;where likes < 20;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field is smaller than or equal to value`() {
        val query = sut.select("id, title, likes")
            .where { "likes".isSmallerThanOrEqualTo("20") }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, likes;where likes <= 20;offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field contains all of values`() {
        val query = sut.select("id, title, genres")
            .where { "genres".containsAllOf(listOf("20", "30", "40")) }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, genres;where genres = [20, 30, 40];offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field does not contain all of values`() {
        val query = sut.select("id, title, genres")
            .where { "genres".doesNotContainAllOf(listOf("20", "30", "40")) }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, genres;where genres = ![20, 30, 40];offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field contains any of values`() {
        val query = sut.select("id, title, genres")
            .where { "genres".containsAnyOf(listOf("20", "30", "40")) }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, genres;where genres = (20, 30, 40);offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field does not contain any of values`() {
        val query = sut.select("id, title, genres")
            .where { "genres".doesNotContainAnyOf(listOf("20", "30", "40")) }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo("fields id, title, genres;where genres = !(20, 30, 40);offset 100;limit 500;sort title asc;")
    }

    @Test
    fun `Builds select query where some field contains exclusively all of values`() {
        val query = sut.select("id, title, genres")
            .where { "genres".containsExclusivelyAllOf(listOf("20", "30", "40")) }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo(
                "fields id, title, genres;where genres = {20, 30, 40};offset 100;" +
                        "limit 500;sort title asc;"
            )
    }

    @Test
    fun `Builds select query with two conditions combined with logical and operator`() {
        val query = sut.select("id, title, genres, likes")
            .where {
                "genres".containsAnyOf(listOf("20", "30", "40")) and
                        { "likes".isLargerThan("20") }
            }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo(
                "fields id, title, genres, likes;where genres = (20, 30, 40) & likes > 20;offset 100;" +
                        "limit 500;sort title asc;"
            )
    }

    @Test
    fun `Builds select query with two conditions combined with logical or operator`() {
        val query = sut.select("id, title, genres, likes")
            .where {
                "genres".containsAnyOf(listOf("20", "30", "40")) or
                        { "likes".isLargerThan("20") }
            }
            .offset(100)
            .limit(500)
            .sortAsc("title")
            .build()

        assertThat(query)
            .isEqualTo(
                "fields id, title, genres, likes;where genres = (20, 30, 40) | likes > 20;" +
                        "offset 100;limit 500;sort title asc;"
            )
    }

    @Test
    fun `Builds a complex select query with lots of conditions`() {
        val query = sut
            .select("id, title, genres, likes, rating, release_date")
            .where {
                "id".containsAnyOf(listOf("1", "2", "3")) and
                        { "title".isNotNull } and
                        { "genres".containsAnyOf(listOf("20", "30", "40")) } and
                        { "likes".isLargerThan("20") } or
                        { "rating".isLargerThanOrEqualTo("80") } or
                        { "release_date".isEqual("2020-03-01") }
            }
            .offset(100)
            .limit(500)
            .sortAsc("id")
            .build()

        assertThat(query)
            .isEqualTo(
                "fields id, title, genres, likes, rating," +
                        " release_date;where id = (1, 2, 3) & title != null & " +
                        "genres = (20, 30, 40) & likes > 20 | rating >= 80 | release_date " +
                        "= 2020-03-01;offset 100;limit 500;" +
                        "sort id asc;"
            )
    }
}
