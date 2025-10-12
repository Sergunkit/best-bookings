import { useEffect, useState, useRef } from 'react';
import {
  FloatButton, Drawer, List, Input, Button, Space,
} from 'antd';
import { MessageOutlined, SendOutlined } from '@ant-design/icons';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import client, { type Message, basePath } from '@api';
import { useAuth } from '~/authContext';

interface UIMessage {
  id: number;
  text: string;
  sender: 'user' | 'support';
  timestamp: Date;
}

const ChatWidget = () => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState<UIMessage[]>([]);
  const { user } = useAuth();
  const clientRef = useRef<Client | null>(null);
  const isConnecting = useRef(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    const setupWebSocket = () => {
      if (isConnecting.current) {
        return null;
      }
      isConnecting.current = true;

      const socket = new SockJS(`${basePath}/ws`);
      const socketClient = new Client({
        webSocketFactory: () => socket,
        connectHeaders: {
          Authorization: `Bearer ${String(localStorage.getItem('token'))}`,
        },
        onConnect: () => {
          socketClient.subscribe(`/user/${String(user?.id)}/queue/messages`, (mes) => {
            const newMessage = JSON.parse(mes.body) as Message;
            setMessages((prev) => [
              ...prev,
              {
                id: newMessage.id,
                text: newMessage.content,
                sender: newMessage.senderId === user?.id ? 'user' : 'support',
                timestamp: new Date(newMessage.createdAt),
              },
            ]);
          });
        },
      });

      clientRef.current = socketClient;
      socketClient.activate();
      return () => {
        isConnecting.current = false;
        // eslint-disable-next-line @typescript-eslint/no-floating-promises
        socketClient.deactivate();
      };
    };

    setupWebSocket();
  }, [user?.id]);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        if (!user?.id) return;
        const apiMessages = await client.messagesGet({ userId: user.id });
        const converted = apiMessages.map((msg: Message) => ({
          id: msg.id,
          text: msg.content,
          sender: msg.senderId === user.id ? 'user' : 'support',
          timestamp: new Date(msg.createdAt),
        })) as UIMessage[];
        setMessages(converted);
      } catch (error) {
        console.error('Ошибка загрузки истории:', error);
      }
    };
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    if (user) fetchHistory();
  }, [user]);

  const handleSendMessage = () => {
    if (!message.trim() || !user || !clientRef.current) return;

    const messageDTO = {
      senderId: user.id,
      receiverId: 1,
      content: message,
    };

    clientRef.current.publish({
      destination: '/app/sendMessage',
      body: JSON.stringify(messageDTO),
    });

    setMessage('');
  };

  return (
    <>
      <FloatButton
        icon={<MessageOutlined />}
        type="primary"
        style={{ right: 24, bottom: 24, width: 56, height: 56 }}
        onClick={() => setOpen(true)}
      />

      <Drawer
        title="Чат с поддержкой"
        placement="right"
        onClose={() => setOpen(false)}
        open={open}
      >
        <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
          {/* История сообщений */}
          <div style={{ flex: 1, overflowY: 'auto', marginBottom: 16 }}>
            <List
              dataSource={messages}
              renderItem={(msg) => (
                <div
                  style={{
                    display: 'flex',
                    justifyContent: msg.sender === 'user' ? 'flex-end' : 'flex-start',
                    marginBottom: 8,
                  }}
                >
                  <div
                    style={{
                      background: msg.sender === 'user' ? '#1890ff' : '#f0f0f0',
                      color: msg.sender === 'user' ? '#fff' : 'rgba(0, 0, 0, 0.85)',
                      padding: '8px 12px',
                      borderRadius: 8,
                      maxWidth: '80%',
                    }}
                  >
                    {msg.text}
                    <div
                      style={{
                        fontSize: 12,
                        color: msg.sender === 'user' ? 'rgba(255,255,255,0.8)' : 'rgba(0,0,0,0.45)',
                        marginTop: 4,
                      }}
                    >
                      {msg.timestamp.toLocaleTimeString()}
                    </div>
                  </div>
                </div>
              )}
            />
            <div ref={messagesEndRef} />
          </div>

          <Space.Compact>
            <Input
              placeholder="Введите сообщение..."
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              onPressEnter={handleSendMessage}
            />
            <Button
              type="primary"
              icon={<SendOutlined />}
              onClick={handleSendMessage}
            />
          </Space.Compact>
        </div>
      </Drawer>
    </>
  );
};

export default ChatWidget;
