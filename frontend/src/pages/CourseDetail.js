import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../utils/api";

export default function CourseDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [contents, setContents] = useState([]);
  const [courseInfo, setCourseInfo] = useState(null);
  const [completedLessons, setCompletedLessons] = useState(new Set());

  useEffect(() => {
    if (id) {
      fetchCourseInfo();
      fetchContents();
      fetchProgress();
    }
  }, [id]);

  const fetchCourseInfo = async () => {
    try {
      const res = await api.get(`/courses/${id}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      setCourseInfo(res.data);
    } catch (err) {
      console.error("Error fetching course info", err);
    }
  };

  const fetchContents = async () => {
    try {
      const res = await api.get(`/contents?courseId=${id}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      // Sort by orderIndex
      const sorted = res.data.sort((a, b) => a.orderIndex - b.orderIndex);
      setContents(sorted);
    } catch (err) {
      console.error("Error fetching contents", err);
    }
  };

  const fetchProgress = async () => {
    try {
      const email = localStorage.getItem("email");
      const res = await api.get(`/progress`, {
        params: {
          studentEmail: email,
          courseId: id,
        },
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });

      console.log("📥 Progress data received:", res.data);

      // Mark completed lessons (percentComplete === 100)
      const completed = new Set(
        res.data
          .filter((progress) => progress.percentComplete >= 100)
          .map((progress) => progress.contentId)
      );
      setCompletedLessons(completed);
      console.log("✅ Loaded progress:", completed.size, "lessons completed");
      console.log("✅ Completed content IDs:", Array.from(completed));
    } catch (err) {
      console.error("❌ Error fetching progress:", err);
      console.error("❌ Error details:", err.response?.data);
    }
  };

  const toggleCompletion = async (contentId) => {
    const isCurrentlyCompleted = completedLessons.has(contentId);
    const newPercentComplete = isCurrentlyCompleted ? 0 : 100;

    // Optimistically update UI
    setCompletedLessons((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(contentId)) {
        newSet.delete(contentId);
      } else {
        newSet.add(contentId);
      }
      return newSet;
    });

    // Save to backend
    try {
      const email = localStorage.getItem("email");
      await api.post(
        `/progress/update`,
        {
          studentEmail: email,
          courseId: parseInt(id),
          contentId: contentId,
          percentComplete: newPercentComplete,
        },
        {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        }
      );
      console.log(
        `✅ Progress saved: Content ${contentId} marked as ${
          newPercentComplete === 100 ? "complete" : "incomplete"
        }`
      );
    } catch (err) {
      console.error("Error updating progress", err);
      // Revert UI on error
      setCompletedLessons((prev) => {
        const newSet = new Set(prev);
        if (newSet.has(contentId)) {
          newSet.delete(contentId);
        } else {
          newSet.add(contentId);
        }
        return newSet;
      });
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

  const progressPercentage =
    contents.length > 0
      ? Math.round((completedLessons.size / contents.length) * 100)
      : 0;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <button
                onClick={() => navigate("/student")}
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
                  {courseInfo?.title || "Course Contents"}
                </h1>
                <p className="text-sm text-gray-600 mt-1">
                  {contents.length} lesson{contents.length !== 1 ? "s" : ""} •{" "}
                  {completedLessons.size} completed
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Progress Section */}
        {contents.length > 0 && (
          <div className="bg-white rounded-lg shadow-md p-6 mb-8">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h2 className="text-lg font-bold text-gray-900">
                  Your Progress
                </h2>
                <p className="text-sm text-gray-600">
                  {completedLessons.size} of {contents.length} lessons completed
                </p>
              </div>
              <div className="text-right">
                <div className="text-3xl font-bold text-green-600">
                  {progressPercentage}%
                </div>
                <div className="text-xs text-gray-500">Complete</div>
              </div>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-3">
              <div
                className="bg-green-500 h-3 rounded-full transition-all duration-500"
                style={{ width: `${progressPercentage}%` }}
              ></div>
            </div>
          </div>
        )}

        {/* Course Contents */}
        <div className="mb-6">
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
                  d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                />
              </svg>
            </div>
            <div>
              <h2 className="text-2xl font-bold text-gray-900">
                Course Contents
              </h2>
              <p className="text-sm text-gray-600">
                Click on any lesson to view
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
                The instructor hasn't added any lessons yet. Check back soon!
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {contents.map((content, index) => (
                <div
                  key={content.id}
                  className={`bg-white rounded-lg shadow-md hover:shadow-lg transition duration-300 p-6 ${
                    completedLessons.has(content.id)
                      ? "border-l-4 border-green-500"
                      : ""
                  }`}
                >
                  <div className="flex items-start">
                    {/* Checkbox */}
                    <button
                      onClick={() => toggleCompletion(content.id)}
                      className={`flex-shrink-0 w-6 h-6 rounded border-2 mr-4 mt-1 flex items-center justify-center transition duration-200 ${
                        completedLessons.has(content.id)
                          ? "bg-green-500 border-green-500"
                          : "border-gray-300 hover:border-green-500"
                      }`}
                    >
                      {completedLessons.has(content.id) && (
                        <svg
                          className="w-4 h-4 text-white"
                          fill="currentColor"
                          viewBox="0 0 20 20"
                        >
                          <path
                            fillRule="evenodd"
                            d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                            clipRule="evenodd"
                          />
                        </svg>
                      )}
                    </button>

                    {/* Order Number */}
                    <div className="flex-shrink-0 w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center mr-4">
                      <span className="text-lg font-bold text-gray-700">
                        {content.orderIndex}
                      </span>
                    </div>

                    {/* Content Info */}
                    <div className="flex-1">
                      <div className="flex items-center mb-2">
                        <span
                          className={`inline-flex items-center px-3 py-1 text-xs font-semibold rounded-full ${getContentTypeBadge(
                            content.contentType
                          )}`}
                        >
                          <span className="mr-1">
                            {getContentTypeIcon(content.contentType)}
                          </span>
                          {content.contentType}
                        </span>
                        {completedLessons.has(content.id) && (
                          <span className="ml-2 inline-flex items-center px-2 py-1 text-xs font-semibold text-green-700 bg-green-100 rounded-full">
                            ✓ Completed
                          </span>
                        )}
                      </div>

                      <h3
                        className={`text-xl font-bold mb-2 ${
                          completedLessons.has(content.id)
                            ? "text-gray-500 line-through"
                            : "text-gray-900"
                        }`}
                      >
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
                          className="inline-flex items-center px-4 py-2 bg-green-500 hover:bg-green-600 text-white font-medium text-sm rounded-md transition duration-200 ease-in-out transform hover:scale-105"
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
                              d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z"
                            />
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                            />
                          </svg>
                          Start Lesson
                        </a>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Completion Message */}
        {contents.length > 0 && completedLessons.size === contents.length && (
          <div className="bg-green-50 border border-green-200 rounded-lg p-6 text-center">
            <svg
              className="w-16 h-16 text-green-500 mx-auto mb-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
            <h3 className="text-2xl font-bold text-green-800 mb-2">
              Congratulations! 🎉
            </h3>
            <p className="text-green-700">
              You've completed all lessons in this course!
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
