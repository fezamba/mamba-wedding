import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LoginPage from '../pages/LoginPage'
import RsvpPage from '../pages/RsvpPage'
import GiftsPage from '../pages/GiftsPage'
import GiftDetailPage from '../pages/GiftDetailPage'
import MessagesPage from '../pages/MessagesPage'
import ProtectedRoute from '../components/ProtectedRoute'

function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LoginPage />} />

        <Route
          path="/rsvp"
          element={
            <ProtectedRoute>
              <RsvpPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/gifts"
          element={
            <ProtectedRoute>
              <GiftsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/gifts/:id"
          element={
            <ProtectedRoute>
              <GiftDetailPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/messages"
          element={
            <ProtectedRoute>
              <MessagesPage />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  )
}

export default AppRoutes