/* eslint-disable max-len */
import {
  List, Typography, Tag, Card, Rate, Button,
} from 'antd';
import dayjs from 'dayjs';
import { Link, useNavigate } from 'react-router';
import { useCallback, useEffect, useState } from 'react';
import client from '@api';
import type { Booking, RoomDto, Hotel } from '@api';
import { useAuth } from '~/authContext';

const { Text, Title } = Typography;

type EnrichedBooking = Booking & {
  room: RoomDto;
  hotel: Hotel;
  totalPrice: number;
};

const BookingsPage = () => {
  const [bookings, setBookings] = useState<EnrichedBooking[]>([]);
  const { user } = useAuth();
  const navigate = useNavigate();

  const fetchBookings = useCallback(async () => {
    if (!user) return;

    const bookingsResponse = await client.userBookingsList({ userId: user.id });
    const enrichedBookings = await Promise.all(
      bookingsResponse.data.map(async (booking) => {
        const room = await client.roomsGet({ roomId: booking.roomId });
        const hotel = await client.hotelsGet({ hotelId: room.hotelId });
        return {
          ...booking,
          room,
          hotel,
          totalPrice: room.price * dayjs(booking.checkOut).diff(dayjs(booking.checkIn), 'days'),
        };
      }),
    );

    setBookings(enrichedBookings);
  }, [user]);

  useEffect(() => {
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    fetchBookings();
  }, [fetchBookings, user]);

  const handleEdit = async (bookingId: number, room: RoomDto, checkIn: Date, checkOut: Date) => {
    await client.bookingsDelete({ bookingId });
    await fetchBookings();
    await navigate(`/booking/${room.id.toString()}?checkIn=${dayjs(checkIn).format('YYYY-MM-DD')}&checkOut=${dayjs(checkOut).format('YYYY-MM-DD')}`);
  };

  const handleDelete = async (bookingId: number) => {
    await client.bookingsDelete({ bookingId });
    await fetchBookings();
  };

  return (
    <Card title="Мои бронирования" styles={{ body: { padding: 0 } }}>
      <List
        itemLayout="vertical"
        dataSource={bookings}
        renderItem={(booking) => (
          <List.Item style={{ margin: 24 }}>
            <List.Item.Meta
              title={(
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <Link to={`/hotels/${String(booking.hotel.id)}`}>{booking.hotel.name}</Link>
                    <Rate disabled defaultValue={booking.hotel.rating} style={{ marginLeft: 16, fontSize: 14 }} />
                  </div>
                  <Tag color={dayjs().isBefore(booking.checkOut) ? 'green' : 'default'}>
                    {dayjs().isBefore(booking.checkIn) ? 'Предстоящее' : 'Завершено'}
                  </Tag>
                </div>
              )}
              description={(
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: 24 }}>
                  <div style={{ flex: 1 }}>
                    <Text strong>Номер: {booking.room.name}</Text>
                    <br />
                    <Text type="secondary">{booking.room.description}</Text>
                    <div style={{ marginTop: 8 }}>
                      <Text>
                        {dayjs(booking.checkIn).format('DD.MM.YYYY')} - {dayjs(booking.checkOut).format('DD.MM.YYYY')}
                      </Text>
                      <br />
                      <Text>{dayjs(booking.checkOut).diff(dayjs(booking.checkIn), 'days')} ночей</Text>
                    </div>
                    <div style={{ marginTop: 8 }}>
                      {booking.room.amenities.map((amenity) => (
                        <Tag key={amenity.id}>{amenity.name}</Tag>
                      ))}
                    </div>
                  </div>
                  <div style={{ textAlign: 'right', minWidth: 120 }}>
                    <Title level={4} style={{ margin: 0 }}>${booking.totalPrice.toFixed(2)}</Title>
                    <Text type="secondary">{booking.room.price}$/ночь</Text>
                    <div style={{ marginTop: 16 }}>
                      <Button
                        type="primary"
                        onClick={() => handleEdit(booking.id, booking.room, booking.checkIn, booking.checkOut)}
                        style={{ marginRight: 8 }}
                      >
                        Изменить даты
                      </Button>
                      <Button color="danger" variant="outlined" onClick={() => handleDelete(Number(booking.id))}>
                        Удалить бронь
                      </Button>
                    </div>
                  </div>
                </div>
              )}
            />
          </List.Item>
        )}
      />
    </Card>
  );
};

export default BookingsPage;
