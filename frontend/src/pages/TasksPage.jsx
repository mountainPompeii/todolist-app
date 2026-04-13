import { useState, useEffect } from 'react'
import {useNavigate} from "react-router-dom";

function TasksPage() {

    const navigate = useNavigate();

    const [tasks, setTasks] = useState([])

    const [meta, setMeta] = useState({ statuses: [], priorities: [] })

    const [editingId, setEditingId] = useState(null)

    const [newTask, setNewTask] = useState({
        title: '',
        description: '',
        priority: 'LOW',
        status: 'TODO'
    })

    // --- ЗАГРУЗКА ДАННЫХ ---
    useEffect(() => {
        fetchAllData();
    }, [])

    const fetchAllData = () => {
        const token = localStorage.getItem("jwt_token");
        if (!token) {
            window.location.href = "/login";
            return;
        }

        fetch('/api/tasks', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(res =>  {
                if (res.status === 403 || res.status === 401) {
                localStorage.removeItem("jwt_token");
                window.location.href = "/login";
                throw new Error("Access denied");
        }
            return res.json();
        })
            .then(data => setTasks(data))
            .catch(err => console.error("Ошибка задач:", err));

        fetch('/api/tasks/metadata', {
            headers: {'Authorization': `Bearer ${token}`}
        })
            .then(res => res.json())
            .then(data => {
                console.log("Metadata:", data); // Проверка в консоли
                setMeta(data);
                if(!editingId) {
                    if (data.priorities.length > 0) setNewTask(prev => ({...prev, priority: data.priorities[0]}));
                    if (data.statuses.length > 0) setNewTask(prev => ({...prev, status: data.statuses[0]}));
                }
            })
            .catch(err => console.error("Metadata error. Check the controller!", err));
    }

    const handleEditTask = (task) => {
        setEditingId(task.id)
        setNewTask({
            title: task.title,
            description: task.description,
            priority: task.priority,
            status: task.status,
        })
    }

    const handleCancelEdit = () => {
        setEditingId(null)
        setNewTask({
            title: '',
            description: '',
            priority: meta.priorities[0],
            status: meta.statuses[0]})
    }

    // --- CREATING THE TASK ---
    const handleSubmit = async () => {
        if (!newTask.description.trim()) return alert("Enter the text of the task!");

        if(editingId) {
            await updateTask();
        } else {
            await createTask();
        }


    }

    const createTask = async () => {
        try {
            const response = await fetch('/api/tasks', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
                },
                body: JSON.stringify(newTask)
            });

            if (response.ok) {
                setNewTask(prev => ({ ...prev,
                    title: '',
                    description: '' }));
                fetchAllData();
            } else {
                alert("Error creating the task");
            }
        } catch (error) {
            console.error(error);
        }
    }

    const updateTask = async () => {
        try {
            const response = await fetch(`/api/tasks/${editingId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
                },
                body: JSON.stringify(newTask)
            });
            if (response.ok) {
                handleCancelEdit();
                fetchAllData();
            } else {
                alert("Update error");
            }
        } catch (error) { console.error(error); }
    }

    const handleDelete = async (id) => {
        if (!window.confirm("Delete the task?")) return;

        await fetch(`/api/tasks/${id}`, { method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
            },});
        setTasks(tasks.filter(t => t.id !== id));
    }

    const handleLogout = () => {
        localStorage.removeItem("jwt_token");
        navigate('/');
    }


    return (
        <div style={{ maxWidth: '600px', margin: '0 auto' }}>
            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                <h1>My tasks</h1>
                <button
                    onClick={handleLogout}
                    style={{backgroundColor: '#555', padding: '5px 10px', fontSize: '14px'}}
                >
                    Log out 🚪
                </button>
            </div>
            <div className="card" style={{ marginBottom: '20px', padding: '15px', border: '1px solid #555' }}>
                <h3>{editingId ? 'Edit task' : 'New task'}</h3>

                <div style={{ display: 'flex', gap: '15px', marginBottom: '15px' }}>
                    <input
                        type="text"
                        placeholder="Enter the task name"
                        value={newTask.title}
                        onChange={(e) => setNewTask({...newTask, title: e.target.value})}
                        style={{ flexGrow: 1 }}
                    />
                </div>
                <div style={{ display: 'flex', gap: '15px', marginBottom: '15px' }}>
                    <input
                        type="text"
                        placeholder="Describe the task"
                        value={newTask.description}
                        onChange={(e) => setNewTask({...newTask, description: e.target.value})}
                        style={{ flexGrow: 1 }}
                    />
                </div>

                <div style={{ display: 'flex', gap: '10px', justifyContent: 'space-between' }}>
                    <select
                        value={newTask.priority}
                        onChange={(e) => setNewTask({...newTask, priority: e.target.value})}
                    >
                        {meta.priorities.map(p => <option key={p} value={p}>{p}</option>)}
                    </select>

                    <select
                        value={newTask.status}
                        onChange={(e) => setNewTask({...newTask, status: e.target.value})}
                    >
                        {meta.statuses.map(s => <option key={s} value={s}>{s}</option>)}
                    </select>

                    <div style={{display: 'flex', gap: '5px'}}>
                        {editingId && (
                            <button onClick={handleCancelEdit} style={{backgroundColor: '#gray'}}>
                                Отмена
                            </button>
                        )}
                        <button onClick={handleSubmit} style={{backgroundColor: editingId ? 'orange' : ''}}>
                            {editingId ? 'Save' : 'Add'}
                        </button>
                    </div>
                </div>
            </div>

            <div className="card">
                {tasks.length > 0 ? (
                    <ul style={{ textAlign: 'left', listStyle: 'none', padding: 0 }}>
                        {tasks.map((task) => (
                            <li key={task.id} style={{
                                padding: '10px',
                                borderBottom: '1px solid #444',
                                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                                backgroundColor: editingId === task.id ? '#333' : 'transparent'
                            }}>
                                <div style={{flexGrow: 1}}>
                                    <span style={{
                                        marginRight: '10px', fontWeight: 'bold',
                                        color: task.priority === 'HIGH' ? 'red' :
                                            task.priority === 'MEDIUM' ? 'orange':
                                                task.priority === 'LOW' ? 'green' :
                                                    'blue'
                                    }}>[{task.priority}]</span>

                                    <span style={{
                                        textDecoration: task.status === 'DONE' ? 'line-through' : 'none',
                                        color: task.status === 'DONE' ? 'gray' : 'inherit'
                                    }}>{task.title}
                                    </span>
                                    <div style={{fontSize: '1.1em', fontWeight:'bold'}}>{task.description}</div>
                                    <div style={{ fontSize: '0.8em', color: '#888' }}>Статус: {task.status}</div>
                                </div>

                                <div>
                                    <button
                                        onClick={() => handleEditTask(task)}
                                        style={{ marginRight: '5px',
                                            padding: '5px 10px',
                                            fontSize: '12px' }}
                                    >
                                        ✏️
                                    </button>

                                    <button
                                        onClick={() => handleDelete(task.id)}
                                        style={{ marginRight: '5px',
                                            backgroundColor: '#ff4444',
                                            padding: '5px 10px',
                                            fontSize: '12px' }}
                                    >
                                        X
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>List is empty</p>
                )}
            </div>
        </div>
    )
}

export default TasksPage