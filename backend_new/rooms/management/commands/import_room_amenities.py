from django.core.management.base import BaseCommand
from rooms.models import Room
from amenities.models import Amenity

class Command(BaseCommand):
    help = 'Imports initial data for room amenities from a predefined structure'

    def handle(self, *args, **options):
        self.stdout.write('Deleting existing room amenity data...')
        for room in Room.objects.all():
            room.amenities.clear()

        self.stdout.write('Importing room amenities...')

        try:
            basic_amenities = Amenity.objects.filter(name__in=['Wi-Fi', 'Кондиционер', 'Телевизор'])
            luxury_amenities = Amenity.objects.filter(name__in=['Мини-бар', 'Сейф'])
            minibar_amenity = Amenity.objects.get(name='Мини-бар')
            safe_amenity = Amenity.objects.get(name='Сейф')

            for room in Room.objects.all():
                # Basic amenities for all rooms
                room.amenities.add(*basic_amenities)

                # Luxury amenities for "люкс" rooms
                if 'люкс' in room.name.lower():
                    room.amenities.add(*luxury_amenities)

                # Special amenities
                if 'бизнес' in room.name.lower():
                    room.amenities.add(minibar_amenity)
                if 'президент' in room.name.lower():
                    room.amenities.add(safe_amenity)
                if 'спа' in room.name.lower():
                    room.amenities.add(safe_amenity)
                if 'кулинарный' in room.name.lower():
                    room.amenities.add(safe_amenity)
                if 'антикварный' in room.name.lower():
                    room.amenities.add(safe_amenity)
                if 'эксклюзивный' in room.name.lower():
                    room.amenities.add(safe_amenity)
        except Amenity.DoesNotExist as e:
            self.stdout.write(self.style.ERROR(f'An amenity was not found: {e}. Please run the amenity import command first.'))
            return

        self.stdout.write(self.style.SUCCESS('Successfully imported room amenities.'))
