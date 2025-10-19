from django.core.management.base import BaseCommand
from amenities.models import Amenity

class Command(BaseCommand):
    help = 'Imports initial data for amenities from a predefined structure'

    def handle(self, *args, **options):
        self.stdout.write('Deleting existing amenity data...')
        Amenity.objects.all().delete()

        amenities_data = [
            {'name': 'Wi-Fi'},
            {'name': 'Кондиционер'},
            {'name': 'Телевизор'},
            {'name': 'Мини-бар'},
            {'name': 'Сейф'},
            {'name': 'Бассейн'},
            {'name': 'Спа'},
            {'name': 'Фитнес-центр'},
            {'name': 'Ресторан'},
            {'name': 'Парковка'},
        ]

        self.stdout.write('Importing amenities...')
        for amenity_data in amenities_data:
            Amenity.objects.create(**amenity_data)

        self.stdout.write(self.style.SUCCESS(f'Successfully imported {len(amenities_data)} amenities.'))
