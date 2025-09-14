import { useEffect, useState } from "react";
import api from "../utils/api";

export default function AdminDashboard() {
    const [users, setUsers] = useState([]);
    const [courses, setCourses] = useState([]);
    const [enrollments, setEnrollments] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const [usersRes, coursesRes, enrollmentsRes] = await Promise.all([
                api.get("/admin/users", {
                    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
                }),
                api.get("/admin/courses", {
                    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
                }),
                api.get("/admin/enrollments", {
                    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
                }),
            ]);

            setUsers(usersRes.data);
            setCourses(coursesRes.data);
            setEnrollments(enrollmentsRes.data);
            setLoading(false);
        } catch (err) {
            console.error("Error fetching admin data", err);
        }
    };

    const handleAssignRole = async (userId, roleName) => {
        try {
            await api.post(`/admin/users/${userId}/role?roleName=${roleName}`, {}, {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
            });
            fetchData();
        } catch (err) {
            console.error("Error assigning role", err);
        }
    };

    const handleDeleteUser = async (userId) => {
        try {
            await api.delete(`/admin/users/${userId}`, {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
            });
            fetchData();
        } catch (err) {
            console.error("Error deleting user", err);
        }
    };

    const handleDeleteCourse = async (courseId) => {
        try {
            await api.delete(`/admin/courses/${courseId}`, {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
            });
            fetchData();
        } catch (err) {
            console.error("Error deleting course", err);
        }
    };

    const handleUpdateEnrollment = async (enrollmentId, status) => {
        try {
            await api.put(`/admin/enrollments/${enrollmentId}/status?status=${status}`, {}, {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
            });
            fetchData();
        } catch (err) {
            console.error("Error updating enrollment", err);
        }
    };

    if (loading) return <p className="p-6">Loading admin dashboard...</p>;

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-6">⚙️ Admin Dashboard</h1>

            {/* Users Section */}
            <div className="mb-8">
                <h2 className="text-xl font-semibold mb-2">👤 Manage Users</h2>
                <table className="w-full border">
                    <thead>
                    <tr className="bg-gray-200">
                        <th className="p-2">ID</th>
                        <th className="p-2">Name</th>
                        <th className="p-2">Email</th>
                        <th className="p-2">Roles</th>
                        <th className="p-2">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map((u) => (
                        <tr key={u.id} className="border-t">
                            <td className="p-2">{u.id}</td>
                            <td className="p-2">{u.fullName}</td>
                            <td className="p-2">{u.email}</td>
                            <td className="p-2">{u.roles.join(", ")}</td>
                            <td className="p-2 space-x-2">
                                <button
                                    onClick={() => handleAssignRole(u.id, "ROLE_TEACHER")}
                                    className="bg-blue-500 text-white px-2 py-1 rounded"
                                >
                                    Make Teacher
                                </button>
                                <button
                                    onClick={() => handleAssignRole(u.id, "ROLE_ADMIN")}
                                    className="bg-green-500 text-white px-2 py-1 rounded"
                                >
                                    Make Admin
                                </button>
                                <button
                                    onClick={() => handleDeleteUser(u.id)}
                                    className="bg-red-500 text-white px-2 py-1 rounded"
                                >
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            {/* Courses Section */}
            <div className="mb-8">
                <h2 className="text-xl font-semibold mb-2">📚 Manage Courses</h2>
                <table className="w-full border">
                    <thead>
                    <tr className="bg-gray-200">
                        <th className="p-2">ID</th>
                        <th className="p-2">Title</th>
                        <th className="p-2">Instructor</th>
                        <th className="p-2">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {courses.map((c) => (
                        <tr key={c.id} className="border-t">
                            <td className="p-2">{c.id}</td>
                            <td className="p-2">{c.title}</td>
                            <td className="p-2">{c.instructorName || "N/A"}</td>
                            <td className="p-2">
                                <button
                                    onClick={() => handleDeleteCourse(c.id)}
                                    className="bg-red-500 text-white px-2 py-1 rounded"
                                >
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            {/* Enrollments Section */}
            <div>
                <h2 className="text-xl font-semibold mb-2">🎓 Manage Enrollments</h2>
                <table className="w-full border">
                    <thead>
                    <tr className="bg-gray-200">
                        <th className="p-2">ID</th>
                        <th className="p-2">Student</th>
                        <th className="p-2">Course</th>
                        <th className="p-2">Status</th>
                        <th className="p-2">Progress %</th>
                        <th className="p-2">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {enrollments.map((e) => (
                        <tr key={e.id} className="border-t">
                            <td className="p-2">{e.id}</td>
                            <td className="p-2">{e.studentName}</td>
                            <td className="p-2">{e.courseTitle}</td>
                            <td className="p-2">{e.status}</td>
                            <td className="p-2">{e.progressPercentage}%</td>
                            <td className="p-2 space-x-2">
                                <button
                                    onClick={() => handleUpdateEnrollment(e.id, "COMPLETED")}
                                    className="bg-green-500 text-white px-2 py-1 rounded"
                                >
                                    Mark Completed
                                </button>
                                <button
                                    onClick={() => handleUpdateEnrollment(e.id, "CANCELLED")}
                                    className="bg-yellow-500 text-white px-2 py-1 rounded"
                                >
                                    Cancel
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
