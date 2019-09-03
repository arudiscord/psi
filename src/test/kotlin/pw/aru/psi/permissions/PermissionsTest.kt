package pw.aru.psi.permissions

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pw.aru.psi.permissions.Permissions.Companion.anyOf
import pw.aru.psi.permissions.Permissions.Companion.none
import pw.aru.psi.permissions.Permissions.Companion.of
import pw.aru.psi.permissions.PermissionsTest.Sample.*

class PermissionsTest {
    enum class Sample : Permission {
        A, B, C, D, E, F;

        override val description: String get() = name
    }

    @Test
    fun `empty permissions`() {
        val permissions = none
        assertTrue(permissions is Permissions.Empty)
        assertTrue(permissions.test(emptySet()))
        assertTrue(permissions.test(setOf(A, B, C)))
    }

    @Test
    fun `single permisson`() {
        val permissions = of(A)
        assertTrue(permissions is Permissions.Single)
        assertTrue(permissions.test(setOf(A)))
        assertTrue(permissions.test(setOf(A, B, C)))
        assertFalse(permissions.test(emptySet()))
    }

    @Test
    fun `any permissons`() {
        val permissions = anyOf(A, B, C)
        assertTrue(permissions is Permissions.Any)
        assertTrue(permissions.test(setOf(A, B, C)))
        assertTrue(permissions.test(setOf(A)))
        assertTrue(permissions.test(setOf(B)))
        assertTrue(permissions.test(setOf(C)))
        assertFalse(permissions.test(setOf(D)))
        assertFalse(permissions.test(emptySet()))
    }

    @Test
    fun `all permissons`() {
        val permissions = of(A, B, C)
        assertTrue(permissions is Permissions.All)
        assertTrue(permissions.test(setOf(A, B, C)))
        assertTrue(permissions.test(setOf(A, B, C, D, E, F)))
        assertFalse(permissions.test(setOf(A)))
        assertFalse(permissions.test(setOf(B)))
        assertFalse(permissions.test(setOf(C)))
        assertFalse(permissions.test(setOf(D)))
        assertFalse(permissions.test(emptySet()))
    }

    @Test
    fun `any multi permissons`() {
        val permissions = anyOf(of(A, B), of(B, C), of(C, D))
        assertTrue(permissions is Permissions.AnyMulti)
        assertTrue(permissions.test(setOf(A, B, C, D)))
        assertTrue(permissions.test(setOf(A, B)))
        assertTrue(permissions.test(setOf(B, C)))
        assertTrue(permissions.test(setOf(C, D)))
        assertFalse(permissions.test(setOf(A, C)))
        assertFalse(permissions.test(setOf(A, D)))
        assertFalse(permissions.test(setOf(B, D)))
        assertFalse(permissions.test(setOf(A)))
        assertFalse(permissions.test(setOf(B)))
        assertFalse(permissions.test(setOf(C)))
        assertFalse(permissions.test(setOf(D)))
        assertFalse(permissions.test(emptySet()))
    }

    @Test
    fun `all multi permissons`() {
        val permissions = of(anyOf(A, B), anyOf(C, D), anyOf(E, F))
        assertTrue(permissions is Permissions.AllMulti)
        assertTrue(permissions.test(setOf(A, B, C, D, E, F)))
        assertTrue(permissions.test(setOf(A, C, E)))
        assertTrue(permissions.test(setOf(B, D, F)))
        assertFalse(permissions.test(setOf(A, B, C)))
        assertFalse(permissions.test(setOf(B, C, D)))
        assertFalse(permissions.test(setOf(D, E, F)))
        assertFalse(permissions.test(setOf(A)))
        assertFalse(permissions.test(setOf(B)))
        assertFalse(permissions.test(setOf(C)))
        assertFalse(permissions.test(setOf(D)))
        assertFalse(permissions.test(emptySet()))
        assertFalse(permissions.test(emptySet()))
    }

    @Test
    fun `any flatten permissons`() {
        val permissions = anyOf(anyOf(A, B), anyOf(B, C), anyOf(C, D))
        assertTrue(permissions is Permissions.Any)
        assertTrue(permissions.test(setOf(A, B, C, D)))
        assertTrue(permissions.test(setOf(A)))
        assertTrue(permissions.test(setOf(B)))
        assertTrue(permissions.test(setOf(C)))
        assertTrue(permissions.test(setOf(D)))
        assertFalse(permissions.test(setOf(E)))
        assertFalse(permissions.test(setOf(F)))
        assertFalse(permissions.test(emptySet()))
    }

    @Test
    fun `all flatten permissons`() {
        val permissions = of(of(A, B), of(C, D), of(E, F))
        assertTrue(permissions is Permissions.All)
        assertTrue(permissions.test(setOf(A, B, C, D, E, F)))
        assertFalse(permissions.test(setOf(A, C, E)))
        assertFalse(permissions.test(setOf(B, D, F)))
        assertFalse(permissions.test(setOf(A, B, C)))
        assertFalse(permissions.test(setOf(B, C, D)))
        assertFalse(permissions.test(setOf(D, E, F)))
        assertFalse(permissions.test(setOf(A)))
        assertFalse(permissions.test(setOf(B)))
        assertFalse(permissions.test(setOf(C)))
        assertFalse(permissions.test(setOf(D)))
        assertFalse(permissions.test(emptySet()))
        assertFalse(permissions.test(emptySet()))
    }
}