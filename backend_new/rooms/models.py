from django.db import models
from hotels.models import Hotel
from amenities.models import Amenity

class Room(models.Model):
    name = models.CharField(max_length=255)
    description = models.TextField()
    price = models.FloatField()
    hotel = models.ForeignKey(Hotel, on_delete=models.CASCADE)
    amenities = models.ManyToManyField(Amenity)

    def amenities_list(self):
        return ", ".join([amenity.name for amenity in self.amenities.all()])
    

    def __str__(self):
        return self.name

# Create your models here.
