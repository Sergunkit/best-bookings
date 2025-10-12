/* eslint-disable max-len */
/* eslint-disable @typescript-eslint/no-floating-promises */
import { useState, useEffect } from 'react';
import client, { type Hotel, type RoomDto } from '@api';
import {
  Rate, Image, Row, Col, Card, Typography, Tag, Space, Divider, Button, Spin, Alert,
} from 'antd';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { ArrowRightOutlined, HeartFilled, HeartOutlined } from '@ant-design/icons';
import { useAuth } from '../authContext';

const { Title, Text } = Typography;

const getRatingColor = (rating: number) => {
  if (rating >= 8) return '#52c41a';
  if (rating >= 6) return '#faad14';
  return '#ff4d4f';
};

type HotelWithFavorite = Hotel & { isFavorite: boolean };

const HotelPage = () => {
  const { hotelId } = useParams<{ hotelId: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const [hotel, setHotel] = useState<HotelWithFavorite | null>(null);
  const [rooms, setRooms] = useState<RoomDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user, isAuthenticated } = useAuth();

  useEffect(() => {
    const loadData = async () => {
      try {
        const id = Number(hotelId);

        const [hotelResponse, roomsResponse] = await Promise.all([
          client.hotelsGet({ hotelId: id }),
          client.hotelRoomsList({ hotelId: id, sortBy: 'price', sortOrder: 'ASC' }),
        ]);

        let favoriteStatus = false;
        if (user) {
          const favoritesResponse = await client.listUserFavorites({ userId: user.id });
          favoriteStatus = favoritesResponse.data.some((fav) => fav.id === id);
        }

        setHotel({ ...hotelResponse, isFavorite: favoriteStatus });
        setRooms(roomsResponse.data);
      } catch {
        setError('Ошибка загрузки данных');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [hotelId, user]);

  const toggleFavorite = async () => {
    if (!user) {
      navigate('/signin', { state: { prevPath: location.pathname } });
      return;
    }

    if (!hotel) return;

    try {
      if (hotel.isFavorite) {
        await client.deleteFavorite({ hotelId: Number(hotelId) });
      } else {
        await client.createFavorite({ favoriteCreateDto: { hotelId: Number(hotelId) } });
      }
      setHotel(() => ({ ...hotel, isFavorite: !hotel.isFavorite }));
    } catch (err) {
      console.error('Ошибка обновления избранного:', err);
    }
  };

  const handleBookingClick = (room: RoomDto) => (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    const proceedBooking = async () => {
      try {
        if (isAuthenticated) {
          await navigate(`/booking/${String(room.id)}`);
        } else {
          await navigate('/signin', { state: { prevPath: `/booking/${String(room.id)}` } });
        }
      } catch (err) {
        console.error('Navigation error:', err);
      }
    };

    proceedBooking();
  };

  if (loading) return <Spin tip="Загрузка..." />;
  if (error) return <Alert message={error} type="error" />;
  if (!hotel) return null;

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      {/* Hotel Info Section */}
      <Row gutter={24} align="middle">
        <Col flex="200px">
          <Image
            width={200}
            height={200}
            style={{ borderRadius: 8 }}
            src={`/hotel-images/${hotel.photoSrc}`}
            preview={false}
          />
        </Col>

        <Col flex="auto">
          <div style={{ position: 'relative' }}>
            <Button
              type="text"
              onClick={toggleFavorite}
              style={{
                position: 'absolute',
                top: 0,
                right: 0,
                padding: 4,
                height: 'auto',
              }}
              aria-label={hotel.isFavorite ? 'Удалить из избранного' : 'Добавить в избранное'}
            >
              {hotel.isFavorite ? (
                <HeartFilled style={{ fontSize: 26, color: 'red' }} />
              ) : (
                <HeartOutlined style={{ fontSize: 26, color: '#bfbfbf' }} />
              )}
            </Button>

            <Title level={2} style={{ marginBottom: 8, paddingRight: 40 }}>
              {hotel.name}
            </Title>

            <Space size="middle" style={{ marginBottom: 16 }}>
              <div>
                <Text strong>Рейтинг:</Text>
                <Tag
                  color={getRatingColor(hotel.rating)}
                  style={{ fontSize: 16, padding: '4px 8px', marginLeft: 8 }}
                >
                  {hotel.rating.toFixed(1)}
                </Tag>
              </div>

              <div>
                <Text strong>Звёзды:</Text>
                <Rate
                  disabled
                  count={5}
                  value={hotel.stars}
                  style={{ marginLeft: 8, fontSize: 16 }}
                />
              </div>
            </Space>

            <Typography.Paragraph type="secondary">
              {hotel.description}
            </Typography.Paragraph>
          </div>
        </Col>
      </Row>

      <Divider />

      <Title level={3} style={{ marginBottom: 24 }}>
        Доступные номера
      </Title>

      <Card styles={{ body: { padding: 0 } }}>
        {rooms.map((room) => (
          <div key={room.id} style={{ padding: 20, paddingBottom: 36, borderBottom: '1px solid #f0f0f0' }}>
            <Row align="middle" gutter={24}>
              <Col flex="auto">
                <Title level={5} style={{ marginTop: 8, marginBottom: 8 }}>
                  {room.name}
                </Title>

                <Typography.Paragraph
                  type="secondary"
                  ellipsis={{ rows: 2 }}
                  style={{ marginBottom: 12 }}
                >
                  {room.description}
                </Typography.Paragraph>

                <Space wrap size={[0, 8]}>
                  {room.amenities.map((amenity) => (
                    <Tag key={amenity.id}>{amenity.name}</Tag>
                  ))}
                </Space>
              </Col>

              <Col>
                <Title level={4} style={{ margin: 0 }}>
                  {room.price} ₽ <Text type="secondary">/ ночь</Text>
                </Title>

                <Button
                  onClick={handleBookingClick(room)}
                  type="primary"
                  style={{ marginTop: 16 }}
                  icon={<ArrowRightOutlined />}
                >
                  Забронировать
                </Button>
              </Col>
            </Row>
          </div>
        ))}
      </Card>
    </div>
  );
};

export default HotelPage;
