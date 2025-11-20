import { useState, useCallback, useEffect } from 'react';
import {
  Avatar, ConfigProvider, Layout, Switch, theme, Flex, Dropdown, Button,
} from 'antd';
import type { MenuProps, MenuTheme } from 'antd';
import { MoonOutlined, SunOutlined, UserOutlined } from '@ant-design/icons';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import ChatWidget from '~/src/components/chatWidget';
import { useAuth } from '../authContext';

const { Header, Content, Footer } = Layout;

const getInitialTheme = (): MenuTheme => {
  const savedTheme = localStorage.getItem('theme');
  return (savedTheme as MenuTheme | null) || 'dark';
};

const BaseLayout: React.FC = () => {
  const [currentTheme, setCurrentTheme] = useState<MenuTheme>(getInitialTheme);
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const changeTheme = (isNewThemeDark: boolean) => {
    const newTheme = isNewThemeDark ? 'dark' : 'light';
    setCurrentTheme(newTheme);
    localStorage.setItem('theme', newTheme);
  };

  useEffect(() => {
    document.documentElement.style.setProperty(
      'color-scheme',
      currentTheme === 'dark' ? 'dark' : 'light',
    );
  }, [currentTheme]);

  const handleLogout = useCallback(() => {
    logout();
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    navigate('/signin');
  }, [logout, navigate]);

  const profileMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      label: <Link to="/profile">Профиль</Link>,
    },
    {
      key: 'bookings',
      label: <Link to="/bookings">Мои бронирования</Link>,
    },
    {
      key: 'favorite',
      label: <Link to="/favorite">Любимые отели</Link>,
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      label: <Link to="/" onClick={handleLogout}>Выход</Link>,
    },
  ];

  return (
    <ConfigProvider
      theme={{
        algorithm: currentTheme === 'light' ? theme.defaultAlgorithm : theme.darkAlgorithm,
        components: {
          Layout: {
            headerBg: currentTheme === 'dark' ? '#141414' : '#001529',
          },
        },
      }}
    >
      <Layout style={{ minHeight: '100vh' }}>
        <Header style={{ padding: '0 24px' }}>
          <Flex justify="space-between" align="center">
            <Link to="/" style={{ display: 'flex', alignItems: 'center' }}>
              <Avatar src="/logo.png" />
              <span style={{ color: 'white', marginLeft: 8, fontSize: 18 }}>Unreal Rooms</span>
            </Link>

            <Flex align="center" gap={16}>
              <Switch
                checked={currentTheme === 'dark'}
                onChange={changeTheme}
                checkedChildren={<MoonOutlined />}
                unCheckedChildren={<SunOutlined />}
                aria-label="Переключить тему"
              />

              {isAuthenticated ? (
                <Dropdown menu={{ items: profileMenuItems }} trigger={['click']}>
                  <Button
                    type="text"
                    style={{ color: 'white' }}
                    icon={<UserOutlined />}
                  >
                    Профиль
                  </Button>
                </Dropdown>
              ) : (
                <Button type="primary" onClick={() => navigate('/signin')}>
                  Войти
                </Button>
              )}
            </Flex>
          </Flex>
        </Header>

        <Content style={{ padding: '24px 48px', flex: 1 }}>
          <main>
            <Outlet />
          </main>
        </Content>

        <Footer style={{ textAlign: 'center' }}>
          Unreal Rooms © {new Date().getFullYear()} Created by Sergunkit
        </Footer>

        {isAuthenticated && <ChatWidget />}
      </Layout>
    </ConfigProvider>
  );
};

export default BaseLayout;
