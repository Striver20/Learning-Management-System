import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../utils/api";

export default function StudentHome() {
    const [enrollments, setEnrollments] = useState([]);
    const [courses, setCourses] = useState([]);
    const email = localStorage.getItem("email");

    useEffect(() => {
        fetchEnrollments();
        fetchCourses();
    }, [email]);

    const fetchEnrollments = async () => {
        try {
            const res = await api.get(`/enrollments/student?email=${email}`, {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
            });
            setEnrollments(res.data);
        } catch (err) {
            console.error("Error fetching enrollments", err);
        }
    };

    const fetchCourses = async () => {
        try {
            const res = await api.get("/courses", {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
            });
            setCourses(res.data);
        } catch (err) {
            console.error("Error fetching courses", err);
        }
    };

    const handleEnroll = async (courseId) => {
        try {
            await api.post(`/enrollments?studentEmail=${email}&courseId=${courseId}`, {}, {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
            });
            fetchEnrollments(); // refresh enrollments after enrolling
        } catch (err) {
            console.error("Error enrolling in course", err);
        }
    };

    // Extract enrolled course IDs for quick lookup
    const enrolledCourseIds = new Set(enrollments.map(e => e.courseId));

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">My Courses</h1>

            {/* Show enrolled courses */}
            {enrollments.length > 0 ? (
                enrollments.map(enroll => (
                    <Link key={enroll.id} to={`/course/${enroll.courseId}`}>
                        <div className="border p-4 mb-3 rounded shadow hover:bg-gray-50">
                            <h2 className="text-lg font-semibold">{enroll.courseTitle}</h2>
                            <p className="text-sm">Progress: {enroll.progressPercentage}%</p>
                        </div>
                    </Link>
                ))
            ) : (
                <p className="text-gray-600">You are not enrolled in any courses yet.</p>
            )}

            <h1 className="text-2xl font-bold mt-6 mb-4">Available Courses</h1>

            {/* Show courses with enroll button */}
            {courses.map(course => (
                <div key={course.id} className="border p-4 mb-3 rounded shadow flex justify-between items-center">
                    <div>
                        <h2 className="text-lg font-semibold">{course.title}</h2>
                        <p className="text-sm text-gray-600">{course.description}</p>
                    </div>
                    {enrolledCourseIds.has(course.id) ? (
                        <span className="text-green-600 font-semibold">✅ Enrolled</span>
                    ) : (
                        <button
                            onClick={() => handleEnroll(course.id)}
                            className="bg-blue-600 text-white px-4 py-2 rounded"
                        >
                            Enroll
                        </button>
                    )}
                </div>
            ))}
        </div>
    );
}
