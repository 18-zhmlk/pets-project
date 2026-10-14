from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from .models import Owner

class OwnerAdmin(admin.ModelAdmin):
    # Поля, которые будут отображаться в списке (list_display)
    list_display = ('username', 'email', 'full_name', 'phone_number', 'is_active') 
    
    # Поля, по которым можно фильтровать список (list_filter)
    list_filter = ('is_active', 'is_staff') 
    
    # Поля для поиска
    search_fields = ('username', 'email', 'full_name')

    # Поля, отображаемые при редактировании/создании (fieldsets)
    fieldsets = (
        (None, {'fields': ('username', 'password_hash')}),
        ('Личная информация', {'fields': ('full_name', 'email', 'phone_number', 'address')}),
        ('Разрешения', {'fields': ('is_active', 'is_staff', 'is_superuser')}),
    )
    
    # Удаляем filter_horizontal, который ссылается на groups и user_permissions
    filter_horizontal = () 

# Регистрируем нашу модель с новым классом Admin
admin.site.register(Owner, OwnerAdmin)