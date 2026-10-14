# accounts/models.py
from django.db import models
from django.utils.translation import gettext_lazy as _
from django.contrib.auth.models import UserManager # <<< ЭТОТ ИМПОРТ НУЖЕН

class Owner(models.Model):
    
    # --- ОБЯЗАТЕЛЬНЫЕ АТРИБУТЫ AUTH ---
    USERNAME_FIELD = 'username' 
    REQUIRED_FIELDS = ['email', 'full_name'] 
    
    # ВРЕМЕННОЕ РЕШЕНИЕ:
    objects = UserManager() # <<< ЭТА СТРОКА НУЖНА ДЛЯ СОЗДАНИЯ СУПЕРПОЛЬЗОВАТЕЛЯ
    
    # ------------------ Поля для аутентификации ------------------
    username = models.CharField(
        max_length=50, 
        unique=True, 
        verbose_name="Имя пользователя для входа"
    )
    email = models.EmailField(
        unique=True, 
        verbose_name="Адрес почты"
    )
    password_hash = models.CharField(
        max_length=128, 
        verbose_name="Хэш пароля"
    ) 

    # --- АТРИБУТЫ СТАТУСА ---
    is_active = models.BooleanField(default=True, verbose_name="Активен")
    is_staff = models.BooleanField(default=False, verbose_name="Персонал")
    is_superuser = models.BooleanField(default=False, verbose_name="Суперпользователь")
    
    # ------------------ Личные данные владельца ------------------
    full_name = models.CharField(
        max_length=100, 
        verbose_name="ФИО"
    )
    phone_number = models.CharField(
        max_length=20, 
        blank=True, 
        null=True, 
        verbose_name="Номер телефона"
    )
    address = models.CharField(
        max_length=255, 
        blank=True, 
        null=True, 
        verbose_name="Адрес проживания"
    )
    
    # --- @property МЕТОДЫ (для работы Auth) ---
    @property
    def is_anonymous(self):
        return False

    @property
    def is_authenticated(self):
        return True
    
    def has_perm(self, perm, obj=None):
        return self.is_superuser
    
    def has_module_perms(self, app_label):
        return self.is_superuser or self.is_staff

    # --- КЛЮЧЕВОЙ МЕТОД: normalize_username ---
    @staticmethod
    def normalize_username(username):
        return username

    class Meta:
        verbose_name = "Владелец"
        verbose_name_plural = "Владельцы"

    def __str__(self):
        return self.full_name