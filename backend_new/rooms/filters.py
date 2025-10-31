from rest_framework.filters import BaseFilterBackend

class RoomFilterBackend(BaseFilterBackend):
    def filter_queryset(self, request, queryset, view):
        filters = {}
        if 'name' in request.query_params:
            filters['name__icontains'] = request.query_params['name']
        if 'minPrice' in request.query_params:
            filters['price__gte'] = request.query_params['minPrice']
        if 'maxPrice' in request.query_params:
            filters['price__lte'] = request.query_params['maxPrice']
        
        return queryset.filter(**filters)
