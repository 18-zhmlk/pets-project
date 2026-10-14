from django.contrib import admin
from .models import Pet

@admin.register(Pet)
class PetAdmin(admin.ModelAdmin):
    list_display = ('id','name','type','owner','created_at')
    list_filter = ('pet_type','sex')
    search_fields = ('name','breed','owner__username')
