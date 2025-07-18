import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ExcelUploader.css';

const ExcelUploader = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleFileChange = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    if (!validateExcelFile(file)) {
      setError('Please upload a valid Excel file (.xlsx or .xls)');
      event.target.value = '';
      return;
    }

    setLoading(true);
    setError('');

    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await fetch('/api/excel/process', {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      // Navigate to ExcelViewer with the processed data
      navigate('/view', { state: { data: result } });
      
    } catch (err) {
      setError('Error processing file: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const validateExcelFile = (file) => {
    const validTypes = [
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'application/vnd.ms-excel'
    ];
    return validTypes.includes(file.type);
  };

  return (
    <div className="excel-uploader">
      <div className="upload-container">
        <h2>Upload Excel File</h2>
        
        <div className="file-input-container">
          <input
            type="file"
            id="excel-file-input"
            accept=".xlsx,.xls"
            onChange={handleFileChange}
            disabled={loading}
          />
          <label htmlFor="excel-file-input" className="file-label">
            {loading ? 'Processing...' : 'Choose Excel File'}
          </label>
        </div>

        {error && <div className="error-message">{error}</div>}
      </div>
    </div>
  );
};

export default ExcelUploader;
