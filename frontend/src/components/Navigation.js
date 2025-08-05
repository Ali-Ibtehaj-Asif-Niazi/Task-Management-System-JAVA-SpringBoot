import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import styles from './Navigation.module.css';

function Navigation() {
  const { isAuthenticated, logout } = useContext(AuthContext);

  return (
    <nav className={styles.nav}>
      <ul className={styles.navList}>
        {isAuthenticated ? (
          <>
            <li className={styles.navItem}>
              <Link to="/projects" className={styles.navLink}>Projects</Link>
            </li>
            <li className={styles.navItem}>
              <Link to="/create-project" className={styles.navLink}>Create Project</Link>
            </li>
            <li className={styles.navItem}>
              <button onClick={logout} className={styles.logoutButton}>Logout</button>
            </li>
          </>
        ) : (
          <>
            <li className={styles.navItem}>
              <Link to="/login" className={styles.navLink}>Login</Link>
            </li>
            <li className={styles.navItem}>
              <Link to="/signup" className={styles.navLink}>Signup</Link>
            </li>
          </>
        )}
      </ul>
    </nav>
  );
}

export default Navigation;