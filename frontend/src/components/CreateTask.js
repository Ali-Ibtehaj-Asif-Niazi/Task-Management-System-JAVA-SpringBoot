import React, { useState, useContext } from 'react';
import { useApi } from '../api';
import { AuthContext } from '../context/AuthContext';

const CreateTask = ({ projectId, onTaskCreated }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const { user } = useContext(AuthContext);
  const API = useApi();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!title.trim()) {
      setError('Title is required');
      return;
    }

    try {
      const taskData = {
        title,
        description,
        projectId,
        userId: user.id
      };

      const response = await API.post('/tasks', taskData);
      console.log('Task created:', response.data);
      setTitle('');
      setDescription('');
      if (onTaskCreated) {
        onTaskCreated(response.data);
      }
    } catch (err) {
      console.error('Error creating task:', err.response?.data || err.message);
      setError(err.response?.data?.message || 'Error creating task. Please try again.');
    }
  };

  return (
    <div>
      <h4>Create New Task</h4>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Task Title"
          required
        />
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Task Description"
        />
        {error && <p className={styles.error}>{error}</p>}
        <button type="submit">Create Task</button>
      </form>
    </div>
  );
};

export default CreateTask;