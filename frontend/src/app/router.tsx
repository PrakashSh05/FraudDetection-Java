import React, { lazy, Suspense } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { MainLayout } from '../layout/MainLayout';
import { LoadingScreen } from '../pages/LoadingScreen';

// React Lazy Load Page Routes
const DashboardPage = lazy(() => import('../features/dashboard/pages/DashboardPage'));
const CasesQueuePage = lazy(() => import('../features/cases/pages/CasesQueuePage'));
const CaseDetailPage = lazy(() => import('../features/caseDetail/pages/CaseDetailPage'));
const InvestigationPage = lazy(() => import('../features/investigation/pages/InvestigationPage'));
const AnalyticsPlaceholder = lazy(() => import('../pages/AnalyticsPlaceholder').then(m => ({ default: m.AnalyticsPlaceholder })));
const NotFoundPage = lazy(() => import('../pages/NotFoundPage').then(m => ({ default: m.NotFoundPage })));

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    errorElement: (
      <Suspense fallback={<LoadingScreen />}>
        <NotFoundPage />
      </Suspense>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/dashboard" replace />,
      },
      {
        path: 'dashboard',
        element: (
          <Suspense fallback={<LoadingScreen />}>
            <DashboardPage />
          </Suspense>
        ),
      },
      {
        path: 'cases',
        element: (
          <Suspense fallback={<LoadingScreen />}>
            <CasesQueuePage />
          </Suspense>
        ),
      },
      {
        path: 'cases/:id',
        element: (
          <Suspense fallback={<LoadingScreen />}>
            <CaseDetailPage />
          </Suspense>
        ),
      },
      {
        path: 'investigation/:transactionId',
        element: (
          <Suspense fallback={<LoadingScreen />}>
            <InvestigationPage />
          </Suspense>
        ),
      },
      {
        path: 'analytics',
        element: (
          <Suspense fallback={<LoadingScreen />}>
            <AnalyticsPlaceholder />
          </Suspense>
        ),
      },
    ],
  },
  {
    path: '*',
    element: (
      <Suspense fallback={<LoadingScreen />}>
        <NotFoundPage />
      </Suspense>
    ),
  },
]);
