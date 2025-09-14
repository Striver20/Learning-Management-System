import jwtDecode from "jwt-decode";

export function getUserRole() {
    const token = localStorage.getItem("token");
    if (!token) return null;
    try {
        const decoded = jwtDecode(token);
        // assuming your token stores roles in "roles" claim
        return decoded.roles?.[0] || null;
    } catch (e) {
        return null;
    }
}
