/* eslint-disable @typescript-eslint/no-floating-promises */

import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  DatePicker,
  Form,
  Button,
  Card,
  Typography,
  Alert,
  Steps,
  Statistic,
} from 'antd';
import client, { type BookingCreateDto } from '@api';
import dayjs, { type Dayjs } from 'dayjs';
import type { RangePickerProps } from 'antd/es/date-picker';
import type { Route } from './+types/booking';

import { useAuth } from '../authContext';

const { RangePicker } = DatePicker;
const { Step } = Steps;
const { Title } = Typography;

export async function clientLoader({ params }: Route.LoaderArgs) {
  const roomId = Number(params.roomId);
  const room = await client.roomsGet({ roomId });
  return { room };
}

const formatDate = (date: Date | Dayjs): string => dayjs(date).format('YYYY-MM-DD');

const BookingPage = ({ loaderData }: Route.ComponentProps) => {
  const { room } = loaderData;
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const { user } = useAuth();
  const location = useLocation();
  const [currentStep, setCurrentStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [availableDates, setAvailableDates] = useState<string[]>([]);
  const [totalPrice, setTotalPrice] = useState(0);
  const [selectedDates, setSelectedDates] = useState<[Dayjs, Dayjs] | undefined>();

  // Загрузка доступных дат
  useEffect(() => {
    const loadAvailability = async () => {
      try {
        const response = await client.roomAvailabilityGet({
          roomId: room.id,
          start: dayjs().endOf('day').toDate(),
          end: dayjs().add(3, 'month').endOf('day').toDate(),
        });

        setAvailableDates(response.dates.map(formatDate));
      } catch {
        setError('Ошибка загрузки доступных дат');
      }
    };
    loadAvailability();
  }, [room.id]);

  const disabledDate: RangePickerProps['disabledDate'] = (current) => (
    !availableDates.includes(formatDate(current.toDate()))
  );

  const calculatePrice = useCallback((
    dates: [Dayjs | null, Dayjs | null] | null,
  ) => {
    if (!dates || !dates[0] || !dates[1]) return;
    const [start, end] = dates;
    const nights = end.diff(start, 'days');
    setTotalPrice(Number((nights * room.price).toFixed(2)));
    setSelectedDates([start, end]);
  }, [room.price]);

  const handleDateStepSubmit = () => {
    if (!selectedDates) {
      setError('Выберите даты бронирования');
      return;
    }
    setCurrentStep(1);
  };

  const handleSubmit = async () => {
    if (!selectedDates) return;

    if (!user) {
      // @typescript-eslint/no-floating-promises
      navigate('/signin', { state: { prevPath: location.pathname } });
      return;
    }

    setLoading(true);
    try {
      const bookingCreateDto: BookingCreateDto = {
        userId: user.id,
        roomId: room.id,
        checkIn: selectedDates[0].startOf('day').toDate(),
        checkOut: selectedDates[1].startOf('day').toDate(),
      };

      await client.bookingsCreate({ bookingCreateDto });
      setCurrentStep(2);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка бронирования');
    } finally {
      setLoading(false);
    }
  };
  // если переход из редактирования бронирования извлекаем из запроса даты
  const searchParams = new URLSearchParams(location.search);
  const initialCheckIn = searchParams.get('checkIn');
  const initialCheckOut = searchParams.get('checkOut');

  useEffect(() => {
    if (initialCheckIn && initialCheckOut) {
      const start = dayjs(initialCheckIn);
      const end = dayjs(initialCheckOut);
      setSelectedDates([start, end]);
      form.setFieldsValue({
        dates: [start, end],
      });
      calculatePrice([start, end]);
    }
  }, [initialCheckIn, initialCheckOut, form, calculatePrice]);

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: 24 }}>
      <Steps current={currentStep} style={{ marginBottom: 40 }}>
        <Step title="Выбор дат" />
        <Step title="Подтверждение" />
      </Steps>

      <Card variant="borderless">
        {error && <Alert message={error} type="error" showIcon style={{ marginBottom: 24 }} />}

        {currentStep === 0 && (
          <Form form={form} onFinish={handleDateStepSubmit}>
            <Title level={4} style={{ marginBottom: 24 }}>Выберите даты проживания</Title>

            <Form.Item
              name="dates"
              rules={[
                { required: true, message: 'Выберите даты бронирования' },
                () => ({
                  validator() {
                    if (!selectedDates) throw new Error();
                    if (selectedDates[1].diff(selectedDates[0], 'days') < 1) {
                      return Promise.reject(new Error('Минимальное время бронирования - 1 ночь'));
                    }
                    return Promise.resolve();
                  },
                }),
              ]}
            >
              <RangePicker
                disabledDate={disabledDate}
                format="DD.MM.YYYY"
                onChange={calculatePrice}
                style={{ width: '100%' }}
              />
            </Form.Item>

            <Statistic
              title="Стоимость"
              value={totalPrice}
              prefix="₽"
              style={{ margin: '24px 0' }}
            />

            <Button type="primary" htmlType="submit" block>
              Продолжить
            </Button>
          </Form>
        )}

        {currentStep === 1 && selectedDates && (
          <div>
            <Title level={4} style={{ marginBottom: 24 }}>Подтверждение бронирования</Title>

            <Statistic
              title="Даты бронирования"
              value={`${formatDate(selectedDates[0])} - ${formatDate(selectedDates[1])}`}
              style={{ marginBottom: 16 }}
            />

            <Statistic
              title="Итоговая стоимость"
              value={totalPrice}
              prefix="₽"
              style={{ marginBottom: 24 }}
            />

            <Button
              type="primary"
              onClick={handleSubmit}
              block
              loading={loading}
              size="large"
            >
              Подтвердить бронирование
            </Button>
          </div>
        )}

        {currentStep === 2 && selectedDates && (
          <div style={{ textAlign: 'center' }}>
            <Title level={4} style={{ marginBottom: 24 }}>Бронирование подтверждено! 🎉</Title>
            <p>
              Номер успешно забронирован с {formatDate(selectedDates[0])} по {formatDate(selectedDates[1])}
            </p>
            <Button
              type="primary"
              onClick={() => navigate('/bookings')}
              style={{ marginTop: 16 }}
            >
              Перейти к списку бронирований
            </Button>
          </div>
        )}
      </Card>
    </div>
  );
};

export default BookingPage;
