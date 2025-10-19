from rest_framework.filters import BaseFilterBackend, OrderingFilter

class CustomOrderingFilter(OrderingFilter):
    ordering_param = "sortBy"

    def get_ordering(self, request, queryset, view):
        sortBy = request.query_params.get(self.ordering_param)
        if sortBy:
            sortOrder = request.query_params.get('sortOrder', 'ASC')
            if sortOrder.upper() == 'DESC':
                return ['-' + sortBy]
            return [sortBy]
        return None

class CustomFilterBackend(BaseFilterBackend):
    def filter_queryset(self, request, queryset, view):
        filters = {}
        if 'name' in request.query_params:
            filters['name__icontains'] = request.query_params['name']
        if 'minRating' in request.query_params:
            filters['rating__gte'] = request.query_params['minRating']
        if 'maxRating' in request.query_params:
            filters['rating__lte'] = request.query_params['maxRating']
        if 'minStars' in request.query_params:
            filters['stars__gte'] = request.query_params['minStars']
        if 'maxStars' in request.query_params:
            filters['stars__lte'] = request.query_params['maxStars']
        
        return queryset.filter(**filters)
