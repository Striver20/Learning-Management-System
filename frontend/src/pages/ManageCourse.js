import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../utils/api";

export default function ManageCourse() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [contents, setContents] = useState([]);
  const [newContent, setNewContent] = useState({
    title: "",
    description: "",
    contentType: "VIDEO",
    orderIndex: 1,
    file: null,
  });
  const [isUploading, setIsUploading] = useState(false);
  const [message, setMessage] = useState("");
  const [courseName, setCourseName] = useState("");
  const [fileName, setFileName] = useState("");

  useEffect(() => {
    fetchContents();
    fetchCourseDetails();
  }, []);

  const fetchCourseDetails = async () => {
    try {
      const res = await api.get(`/courses/${id}`);
      setCourseName(res.data.title);
    } catch (err) {
      console.error("Error fetching course details", err);
    }
  };

  const fetchContents = async () => {
    try {
      const res = await api.get(`/contents?courseId=${id}`);
      // Sort by orderIndex
      const sorted = res.data.sort((a, b) => a.orderIndex - b.orderIndex);
      setContents(sorted);
    } catch (err) {
      console.error("Error fetching contents", err);
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setNewContent({ ...newContent, file });
      setFileName(file.name);
    }
  };

  const handleAddContent = async (e) => {
    e.preventDefault();

    if (!newContent.title || !newContent.description || !newContent.file) {
      setMessage("Please fill in all fields and select a file");
      return;
    }

    setIsUploading(true);
    setMessage("");

    try {
      const formData = new FormData();
      formData.append("courseId", id);
      formData.append("title", newContent.title);
      formData.append("description", newContent.description);
      formData.append("contentType", newContent.contentType);
      formData.append("orderIndex", newContent.orderIndex);
      formData.append("file", newContent.file);

      await api.post("/contents/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });

      setMessage("Content uploaded successfully!");
      setNewContent({
        title: "",
        description: "",
        file: null,
        contentType: "VIDEO",
        orderIndex: contents.length + 1,
      });
      setFileName("");
      fetchContents();

      setTimeout(() => setMessage(""), 3000);
    } catch (err) {
      console.error("Error adding content", err);
      setMessage("Failed to upload content. Please try again.");
    } finally {
      setIsUploading(false);
    }
  };

  const handleDeleteContent = async (contentId) => {
    if (!window.confirm("Are you sure you want to delete this content?"))
      return;

    try {
      await api.delete(`/contents/${contentId}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      fetchContents();
      setMessage("Content deleted successfully");
      setTimeout(() => setMessage(""), 3000);
    } catch (err) {
      console.error("Error deleting content", err);
      setMessage("Failed to delete content");
    }
  };

  const getContentTypeIcon = (type) => {
    if (type === "VIDEO")
      return (
        <svg
          className="w-5 h-5"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z"
          />
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
      );
    if (type === "PDF")
      return (
        <svg
          className="w-5 h-5"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z"
          />
        </svg>
      );
    return (
      <svg
        className="w-5 h-5"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
        />
      </svg>
    );
  };

  const getContentTypeBadge = (type) => {
    const colors = {
      VIDEO: "bg-purple-100 text-purple-700",
      PDF: "bg-red-100 text-red-700",
      DOC: "bg-blue-100 text-blue-700",
    };
    return colors[type] || "bg-gray-100 text-gray-700";
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <button
                onClick={() => navigate("/teacher")}
                className="mr-4 text-gray-600 hover:text-gray-900 transition duration-200"
              >
                <svg
                  className="w-6 h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M15 19l-7-7 7-7"
                  />
                </svg>
              </button>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">
                  Manage Course Content
                </h1>
                <p className="text-sm text-gray-600 mt-1">{courseName}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Add Content Section */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <div className="flex items-center mb-6">
            <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center mr-3">
              <svg
                className="w-6 h-6 text-green-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                />
              </svg>
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900">Add Content</h2>
              <p className="text-sm text-gray-600">
                Upload video, PDF, or document to this course
              </p>
            </div>
          </div>

          <form onSubmit={handleAddContent}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Title */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Content Title *
                </label>
                <input
                  type="text"
                  placeholder="e.g., Introduction to HTML"
                  className="w-full border border-gray-300 rounded-md px-4 py-3 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent transition duration-200"
                  value={newContent.title}
                  onChange={(e) =>
                    setNewContent({ ...newContent, title: e.target.value })
                  }
                  required
                />
              </div>

              {/* Content Type */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Content Type *
                </label>
                <select
                  className="w-full border border-gray-300 rounded-md px-4 py-3 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent transition duration-200"
                  value={newContent.contentType}
                  onChange={(e) =>
                    setNewContent({
                      ...newContent,
                      contentType: e.target.value,
                    })
                  }
                >
                  <option value="VIDEO">🎥 Video</option>
                  <option value="PDF">📄 PDF Document</option>
                  <option value="DOC">📝 Document</option>
                </select>
              </div>
            </div>

            {/* Description */}
            <div className="mt-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Description *
              </label>
              <textarea
                placeholder="Describe what students will learn from this content..."
                className="w-full border border-gray-300 rounded-md px-4 py-3 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent transition duration-200 resize-none"
                rows="3"
                value={newContent.description}
                onChange={(e) =>
                  setNewContent({
                    ...newContent,
                    description: e.target.value,
                  })
                }
                required
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
              {/* Order Index */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Lesson Order *
                </label>
                <input
                  type="number"
                  min="1"
                  placeholder="1"
                  className="w-full border border-gray-300 rounded-md px-4 py-3 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent transition duration-200"
                  value={newContent.orderIndex}
                  onChange={(e) =>
                    setNewContent({
                      ...newContent,
                      orderIndex: e.target.value,
                    })
                  }
                  required
                />
                <p className="text-xs text-gray-500 mt-1">
                  Lower numbers appear first
                </p>
              </div>

              {/* File Upload */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Upload File *
                </label>
                <div className="relative">
                  <input
                    type="file"
                    id="file-upload"
                    className="hidden"
                    onChange={handleFileChange}
                    required
                  />
                  <label
                    htmlFor="file-upload"
                    className="flex items-center justify-center w-full border-2 border-dashed border-gray-300 rounded-md px-4 py-3 cursor-pointer hover:border-green-500 hover:bg-green-50 transition duration-200"
                  >
                    {fileName ? (
                      <span className="text-sm text-gray-700 truncate">
                        📎 {fileName}
                      </span>
                    ) : (
                      <span className="text-sm text-gray-500">Choose File</span>
                    )}
                  </label>
                </div>
              </div>
            </div>

            {/* Submit Button */}
            <div className="flex items-center justify-between mt-6 pt-4 border-t">
              <button
                type="submit"
                disabled={isUploading}
                className="bg-green-500 hover:bg-green-600 text-white font-medium px-6 py-3 rounded-md transition duration-200 ease-in-out transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
              >
                {isUploading ? "Uploading..." : "Upload Content"}
              </button>

              {message && (
                <p
                  className={`text-sm font-medium ${
                    message.includes("success")
                      ? "text-green-600"
                      : "text-red-600"
                  }`}
                >
                  {message}
                </p>
              )}
            </div>
          </form>
        </div>

        {/* Course Contents List */}
        <div className="mb-6">
          <div className="flex items-center mb-6">
            <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center mr-3">
              <svg
                className="w-6 h-6 text-blue-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"
                />
              </svg>
            </div>
            <div>
              <h2 className="text-2xl font-bold text-gray-900">
                Course Contents
              </h2>
              <p className="text-sm text-gray-600">
                {contents.length} lesson{contents.length !== 1 ? "s" : ""} in
                total
              </p>
            </div>
          </div>

          {contents.length === 0 ? (
            <div className="bg-white rounded-lg shadow-md p-12 text-center">
              <svg
                className="w-16 h-16 text-gray-400 mx-auto mb-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                />
              </svg>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">
                No content yet
              </h3>
              <p className="text-gray-600 mb-4">
                Upload your first lesson to get started!
              </p>
            </div>
          ) : (
            <div className="space-y-4">
              {contents.map((content, index) => (
                <div
                  key={content.id}
                  className="bg-white rounded-lg shadow-md hover:shadow-lg transition duration-300 p-6"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex items-start flex-1">
                      {/* Order Number */}
                      <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center mr-4 flex-shrink-0">
                        <span className="text-lg font-bold text-gray-700">
                          {content.orderIndex}
                        </span>
                      </div>

                      {/* Content Info */}
                      <div className="flex-1">
                        <div className="flex items-center mb-2">
                          <span
                            className={`inline-flex items-center px-3 py-1 text-xs font-semibold rounded-full mr-2 ${getContentTypeBadge(
                              content.contentType
                            )}`}
                          >
                            <span className="mr-1">
                              {getContentTypeIcon(content.contentType)}
                            </span>
                            {content.contentType}
                          </span>
                        </div>

                        <h3 className="text-xl font-bold text-gray-900 mb-2">
                          {content.title}
                        </h3>

                        <p className="text-gray-600 text-sm mb-4">
                          {content.description}
                        </p>

                        {content.fileUrl && (
                          <a
                            href={content.fileUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="inline-flex items-center text-green-600 hover:text-green-700 font-medium text-sm group"
                          >
                            <svg
                              className="w-4 h-4 mr-1"
                              fill="none"
                              stroke="currentColor"
                              viewBox="0 0 24 24"
                            >
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                              />
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                              />
                            </svg>
                            View File
                            <svg
                              className="w-4 h-4 ml-1 group-hover:translate-x-1 transition duration-200"
                              fill="none"
                              stroke="currentColor"
                              viewBox="0 0 24 24"
                            >
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M9 5l7 7-7 7"
                              />
                            </svg>
                          </a>
                        )}
                      </div>
                    </div>

                    {/* Delete Button */}
                    <button
                      onClick={() => handleDeleteContent(content.id)}
                      className="ml-4 flex items-center px-4 py-2 bg-red-50 text-red-600 rounded-md hover:bg-red-600 hover:text-white transition duration-200"
                    >
                      <svg
                        className="w-4 h-4 mr-2"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                        />
                      </svg>
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
