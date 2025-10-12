import { useState } from 'react';
import {
  Card,
  Form,
  Input,
  Button,
  Avatar,
  Typography,
  Modal,
  message,
} from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, DeleteOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router';
import client from '@api';
import { useAuth } from '~/authContext';

const { Title, Text } = Typography;

const ProfilePage = () => {
  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();

  const handleProfileUpdate = async (values: { name?: string, email?: string, password?: string }) => {
    if (!user) return;

    try {
      setLoading(true);
      const updatedUser = await client.usersUpdate({
        userId: user.id,
        userUpdateDto: { ...user, ...values },
      });
      login(updatedUser);
      messageApi.open({
        type: 'success',
        content: 'Профиль успешно обновлён',
      });
    } catch {
      messageApi.open({
        type: 'error',
        content: 'Ошибка при обновлении профиля',
      });
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordChange = async (values: { password?: string }) => {
    if (!user) return;

    try {
      setLoading(true);
      const updatedUser = await client.usersUpdate({
        userId: user.id,
        userUpdateDto: { ...user, ...values },
      });
      login(updatedUser);
      messageApi.open({
        type: 'success',
        content: 'Пароль успешно обновлён',
      });
    } catch {
      messageApi.open({
        type: 'error',
        content: 'Ошибка при обновлении пароля',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteAccount = () => {
    Modal.confirm({
      title: 'Удаление аккаунта',
      content: (
        <div>
          <Text strong>Вы уверены что хотите удалить аккаунт?</Text>
          <br />
          <Text type="secondary">Все ваши данные будут безвозвратно удалены</Text>
        </div>
      ),
      okText: 'Удалить',
      okType: 'danger',
      cancelText: 'Отмена',
      async onOk() {
        if (!user) return;
        await client.usersDelete({ userId: user.id });
        message.success('Аккаунт успешно удалён');
        // eslint-disable-next-line @typescript-eslint/no-floating-promises
        navigate('/');
      },
    });
  };

  if (!user) {
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    return navigate('/');
  }

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 24 }}>

      {contextHolder}
      {/* Заголовок и аватар */}
      <div style={{ textAlign: 'center', marginBottom: 32 }}>
        <Avatar
          size={128}
          icon={<UserOutlined />}
          style={{ marginBottom: 16 }}
        />
        <Title level={3}>{user.name}</Title>
        <Text type="secondary">{user.email}</Text>
      </div>

      {/* Основная информация */}
      <Card
        title={<><UserOutlined /> Личные данные</>}
        style={{ marginBottom: 24 }}
      >
        <Form
          form={form}
          initialValues={user}
          onFinish={handleProfileUpdate}
          layout="vertical"
        >
          <Form.Item
            name="name"
            label="Имя"
            rules={[{ required: true, message: 'Введите ваше имя' }]}
          >
            <Input placeholder="Ваше имя" />
          </Form.Item>

          <Form.Item
            name="email"
            label="Email"
            rules={[{ type: 'email', message: 'Некорректный email' }]}
          >
            <Input
              prefix={<MailOutlined />}
              // disabled
              placeholder="Email"
            />
          </Form.Item>

          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
          >
            Сохранить изменения
          </Button>
        </Form>
      </Card>

      {/* Смена пароля */}
      <Card
        title={<><LockOutlined /> Безопасность</>}
        style={{ marginBottom: 24 }}
      >
        <Form
          form={passwordForm}
          onFinish={handlePasswordChange}
          layout="vertical"
        >

          <Form.Item
            name="newPassword"
            label="Новый пароль"
            rules={[{
              required: true,
              min: 8,
              message: 'Пароль должен содержать минимум 8 символов',
            }]}
          >
            <Input.Password placeholder="••••••••" />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            label="Подтверждение пароля"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: 'Введите пароль ещё раз' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('Пароли не совпадают'));
                },
              }),
            ]}
          >
            <Input.Password placeholder="••••••••" />
          </Form.Item>

          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
          >
            Сменить пароль
          </Button>
        </Form>
      </Card>

      {/* Опасная зона */}
      <Card
        title={<><DeleteOutlined /> Опасная зона</>}
        style={{ borderColor: '#ff4d4f' }}
        styles={{ header: { color: '#ff4d4f' } }}
      >
        <div style={{ textAlign: 'center' }}>
          <Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
            Удаление аккаунта невозможно отменить. Все данные будут удалены.
          </Text>
          <Button
            danger
            onClick={handleDeleteAccount}
            icon={<DeleteOutlined />}
          >
            Удалить аккаунт
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default ProfilePage;
