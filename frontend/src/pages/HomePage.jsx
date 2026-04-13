import { useState } from 'react'
import { useNavigate} from "react-router-dom";
import telegramLogo from '../assets/telegram-svgrepo-com.svg'
import githubLogo from '../assets/github-mark.svg'
import linkedinLogo from '../assets/Linkedin SVG Icon.svg'

function HomePage() {

    const [count, setCount] = useState(0)
    const navigate = useNavigate();

    return (
        <>
            <div>
                <a href="https://t.me/s/ice_blue_0" target="_blank">
                    <img src={telegramLogo}
                         className="logo react"
                         alt="Telegram logo"
                         width="100"
                    />
                </a>
                <a href="https://github.com/sleepwalker746" target="_blank">
                    <img src={githubLogo}
                         className="logo react"
                         alt="GitHub logo"
                         width="100"/>
                </a>
                <a href="https://www.linkedin.com/in/arsenii-sidorovych-25603330b/" target="_blank">
                    <img src={linkedinLogo}
                         className="logo react"
                         alt="Linkedin logo"
                         width="100"/>
                </a>
            </div>
            <h1>Август + & Co </h1>
            <div className="card" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '10px' }}>
                <div style={{ display: 'flex', gap: '10px' }}>
                    <button onClick={() => setCount((count) => count + 1)}>
                        Счётчик {count}
                    </button>
                    <button onClick={() => navigate('/registration')}>
                        Go to registration
                    </button>
                </div>

                <button onClick={() => navigate('/login')}>
                    Or log in
                </button>

            </div>
            <p className="read-the-docs">
                All my links above
            </p>
        </>
    )
}
export default HomePage;