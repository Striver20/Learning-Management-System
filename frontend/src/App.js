import { BrowserRouter, Routes, Route } from "react-router-dom";
import Register from "./pages/Register";
import Login from "./pages/Login";
import StudentHome from "./pages/StudentHome";
import CoursePage from "./pages/CoursePage";
import TeacherDashboard from "./pages/TeacherDashboard";
import ManageCourse from "./pages/ManageCourse";
import AdminDashboard from "./pages/AdminDashboard";
import CourseDetails from "./pages/CourseDetail";
function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/register" element={<Register />} />
                <Route path="/" element={<Login />} />
                <Route path="/admin" element={<AdminDashboard />} />
                <Route path="/student" element={<StudentHome />} />
                <Route path="/course/:id" element={<CourseDetails />} />
                <Route path="/teacher" element={<TeacherDashboard />} />
                <Route path="/teacher/course/:id" element={<ManageCourse />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
