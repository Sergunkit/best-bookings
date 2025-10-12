import { useEffect, useState, useRef, useCallback } from 'react';
import {
  List, Avatar, Card, Typography, Row, Col, Input, Button, Space,
} from 'antd';
import { UserOutlined, SendOutlined } from '@ant-design/icons';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import client, { type Message, basePath } from '@api';
import { useAuth } from '~/authContext';

const { Text } = Typography;

interface Chat {
  userId: number;
  clientName: string;
  lastMessage: string;
  messages: Message[];
}

const ChatPage = () => {
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const [message, setMessage] = useState('');
  const [chats, setChats] = useState<Chat[]>([]);
  const { user } = useAuth();
  const clientRef = useRef<Client | null>(null);
  const isConnecting = useRef(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const updateChats = useCallback((prev: Chat[], newMessage: Message): Chat[] => {
    const userId = newMessage.senderId === user?.id ? newMessage.receiverId : newMessage.senderId;
    const existing = prev.find((c) => c.userId === userId);

    return existing
      ? prev.map((c) => (c.userId === userId ? {
        ...c,
        messages: [...c.messages, newMessage],
        lastMessage: newMessage.content,
      } : c))
      : [...prev, {
        userId,
        clientName: `User ${String(userId)}`,
        lastMessage: newMessage.content,
        messages: [newMessage],
      }];
  }, [user?.id]);

  useEffect(() => {
    // Загрузка всех сообщений для менеджера
    const fetchMessages = async () => {
      try {
        const messages = await client.messagesList();
        const grouped = messages.reduce<Record<string, Chat>>((acc: Record<string, Chat>, msg: Message) => {
          const userId = msg.senderId === user?.id ? msg.receiverId : msg.senderId;
          // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
          if (!acc[userId]) {
            acc[userId] = {
              userId,
              clientName: `User ${String(userId)}`,
              lastMessage: msg.content,
              messages: [msg],
            };
          } else {
            acc[userId].messages.push(msg);
            acc[userId].lastMessage = msg.content;
          }
          return acc;
        }, {});

        setChats(Object.values(grouped));
      } catch (error) {
        console.error('Ошибка при загрузке сообщений:', error);
      }
    };

    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    fetchMessages();
  }, [user]);

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
            setChats((prev) => updateChats(prev, newMessage));
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
  }, [updateChats, user?.id]);

  const handleSendMessage = () => {
    if (!message.trim() || !user || !clientRef.current) return;

    const messageDTO = {
      senderId: user.id,
      receiverId: selectedUserId,
      content: message,
    };

    clientRef.current.publish({
      destination: '/app/sendMessage',
      body: JSON.stringify(messageDTO),
    });

    setMessage('');
  };

  const selectedChat = chats.find((c) => c.userId === selectedUserId);

  useEffect(() => {
    scrollToBottom();
  }, [selectedChat?.messages]);

  return (
    <Card styles={{ body: { padding: 0 } }}>
      <Row style={{ height: '70vh' }}>
        <Col span={8} style={{ borderRight: '1px solid #f0f0f0' }}>
          <List
            dataSource={chats}
            renderItem={(item) => (
              <List.Item
                onClick={() => setSelectedUserId(item.userId)}
                style={{
                  cursor: 'pointer',
                  background: selectedUserId === item.userId ? '#595959' : undefined,
                  padding: '12px 24px',
                }}
              >
                <List.Item.Meta
                  avatar={<Avatar icon={<UserOutlined />} />}
                  title={item.clientName}
                  description={(
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <Text ellipsis style={{ maxWidth: '70%' }}>{item.lastMessage}</Text>
                    </div>
                  )}
                />
              </List.Item>
            )}
          />
        </Col>

        <Col span={16}>
          {selectedChat ? (
            <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
              <div style={{ padding: 16, borderBottom: '1px solid #f0f0f0' }}>
                <Text strong>{selectedChat.clientName}</Text>
              </div>

              {/* Сообщения */}
              <div style={{
                flex: 1,
                overflowY: 'auto',
                padding: 16,
                display: 'flex',
                flexDirection: 'column',
                height: 'calc(100vh - 220px)',
                maxHeight: 'calc(100vh - 220px)',
                position: 'relative',
              }}
              >
                <div style={{
                  position: 'absolute',
                  top: 10,
                  left: 10,
                  right: 10,
                  bottom: 10,
                  overflowY: 'auto',
                  paddingRight: 8,
                }}
                >
                  {selectedChat.messages.map((msg) => (
                    <div
                      key={msg.id}
                      style={{
                        display: 'flex',
                        justifyContent: msg.senderId === user?.id ? 'flex-end' : 'flex-start',
                        marginBottom: 8,
                        flexShrink: 0,
                        minHeight: 42,
                      }}
                    >
                      <div style={{
                        background: msg.senderId === user?.id ? '#1890ff' : '#fff',
                        color: msg.senderId === user?.id ? '#fff' : 'rgba(0, 0, 0, 0.85)',
                        padding: '8px 12px',
                        borderRadius: 8,
                        maxWidth: '70%',
                        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                        wordBreak: 'break-word',
                        overflow: 'hidden',
                        overflowWrap: 'anywhere',
                      }}
                      >
                        {msg.content}
                        <div style={{
                          fontSize: 12,
                          // eslint-disable-next-line max-len
                          color: msg.senderId === user?.id ? 'rgba(255,255,255,0.8)' : 'rgba(0,0,0,0.45)',
                          marginTop: 4,
                          whiteSpace: 'nowrap',
                          textOverflow: 'ellipsis',
                          overflow: 'hidden',
                        }}
                        >
                          {new Date(msg.createdAt).toLocaleTimeString()}
                        </div>
                      </div>
                    </div>
                  ))}
                  <div ref={messagesEndRef} />
                </div>
              </div>

              <div style={{ padding: 16, borderTop: '1px solid #f0f0f0' }}>
                <Space.Compact style={{ width: '100%' }}>
                  <Input
                    placeholder="Введите сообщение..."
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    onPressEnter={handleSendMessage}
                    style={{ width: 'calc(100% - 60px)' }}
                  />
                  <Button
                    type="primary"
                    icon={<SendOutlined />}
                    onClick={handleSendMessage}
                    style={{ width: 60 }}
                  />
                </Space.Compact>
              </div>
            </div>
          ) : (
            <div style={{
              height: '100%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#666',
            }}
            >
              <Text>Выберите чат</Text>
            </div>
          )}
        </Col>
      </Row>
    </Card>
  );
};

export default ChatPage;
