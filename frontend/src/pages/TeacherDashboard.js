import { useEffect, useState } from "react";
import api from "../utils/api";

export default function TeacherDashboard() {
    const [courses, setCourses] = useState([]);
    const [newCourse, setNewCourse] = useState({ title: "", description: "", category: "" });

    useEffect(() => {
        fetchCourses();
    }, []);

    const fetchCourses = async () => {
        try {
            const email = localStorage.getItem("email"); // teacher email
            const res = await api.get(`/courses/instructor?email=${email}`);
            setCourses(res.data);
        } catch (err) {
            console.error("Error fetching courses", err);
        }
    };

    const handleCreateCourse = async () => {
        try {
            const email = localStorage.getItem("email");
            await api.post(`/courses?instructorEmail=${email}`, newCourse);
            setNewCourse({ title: "", description: "", category: "" });
            fetchCourses();
        } catch (err) {
            console.error("Error creating course", err);
        }
    };

    return (
        <div className="p-6">
            <h1 className="text-xl font-bold mb-4">📚 Teacher Dashboard</h1>

            {/* Create new course form */}
            <div className="p-4 border rounded mb-6">
                <h2 className="font-semibold mb-2">Create New Course</h2>
                <input
                    type="text"
                    placeholder="Title"
                    className="border p-2 w-full mb-2"
                    value={newCourse.title}
                    onChange={(e) => setNewCourse({ ...newCourse, title: e.target.value })}
                />
                <textarea
                    placeholder="Description"
                    className="border p-2 w-full mb-2"
                    value={newCourse.description}
                    onChange={(e) => setNewCourse({ ...newCourse, description: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Category"
                    className="border p-2 w-full mb-2"
                    value={newCourse.category}
                    onChange={(e) => setNewCourse({ ...newCourse, category: e.target.value })}
                />
                <button
                    onClick={handleCreateCourse}
                    className="bg-blue-600 text-white px-4 py-2 rounded"
                >
                    Create
                </button>
            </div>

            {/* List courses */}
            <h2 className="text-lg font-semibold mb-2">My Courses</h2>
            <ul>
                {courses.map((course) => (
                    <li key={course.id} className="p-4 border rounded mb-2 flex justify-between">
                        <div>
                            <h3 className="font-bold">{course.title}</h3>
                            <p>{course.description}</p>
                            <span className="text-sm text-gray-500">Category: {course.category}</span>
                        </div>
                        <a
                            href={`/teacher/course/${course.id}`}
                            className="text-blue-600 underline"
                        >
                            Manage
                        </a>
                    </li>
                ))}
            </ul>
        </div>
    );
}
