import React from 'react';
import { HashRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header/Header';
import ExcelUploader from './components/ExcelUploader/ExcelUploader';
import ExcelViewer from './components/ExcelViewer/ExcelViewer';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app">
        <Header />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<ExcelUploader />} />
            <Route path="/view" element={<ExcelViewer />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
