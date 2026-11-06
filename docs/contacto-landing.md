# Contacto de la landing

`POST /api/contacto` recibe consultas de visitantes sin iniciar sesión. Reutiliza `CorreoService` y la conexión SMTP que ya usa la recuperación de contraseña.

## Solicitud

```json
{
  "nombre": "Ana Pérez",
  "correo": "ana@example.com",
  "asunto": "Consulta sobre grupos",
  "mensaje": "¿Cómo puedo encontrar un grupo de mi curso?"
}
```

Los cuatro campos son obligatorios. Límites: nombre 100, correo 254, asunto 150 y mensaje 5000 caracteres. Se eliminan los espacios de los extremos. Nombre, correo y asunto no admiten saltos de línea. El visitante puede usar un correo de cualquier dominio válido.

La aplicación envía un correo de texto al equipo de soporte: `From` es la cuenta SMTP configurada y `Reply-To` es el correo del visitante. Al responder desde la bandeja de soporte, la respuesta se dirige al visitante. La solicitud no puede elegir otro destinatario. No se envía una segunda confirmación automática al visitante ni se guardan las consultas en una tabla.

## Configuración

Se conservan `MAIL_USERNAME` y `MAIL_PASSWORD` del envío de recuperación. En Gmail, `MAIL_PASSWORD` es la contraseña de aplicación que ya utiliza el proyecto.

`CONTACT_MAIL_TO` permite indicar la bandeja que recibirá las consultas. Su valor predeterminado es `yachani.soporte@gmail.com`, el correo publicado en Home. Si el equipo recibe consultas en otra bandeja, definir esta variable y actualizar también el correo visible en el frontend.

Ejemplo en PowerShell, suponiendo que las variables de base de datos, JWT y SMTP ya están configuradas:

```powershell
$env:CONTACT_MAIL_TO = "yachani.soporte@gmail.com"
.\mvnw.cmd spring-boot:run
```

No requiere migración SQL. Se conserva el CORS global. El frontend debe apuntar a la API correspondiente mediante su configuración de entorno.

## Respuestas

| Estado | Significado |
| --- | --- |
| 200 | El servidor SMTP aceptó el envío; devuelve `{"mensaje":"Tu mensaje fue enviado. Te responderemos al correo que indicaste."}`. |
| 400 | Campos ausentes, inválidos o demasiado largos. Devuelve `mensaje`. |
| 429 | Límite de intentos. Devuelve `codigo: LIMITE_CONTACTO`, `mensaje` y la cabecera `Retry-After` en segundos. |
| 503 | Configuración de correo incompleta o fallo SMTP. Devuelve `mensaje`; el frontend conserva el formulario. |

El límite es de tres intentos cada quince minutos por dirección de conexión y por instancia de la aplicación; incluye los fallos de SMTP. El almacenamiento del límite es temporal y acotado. No se confía en `X-Forwarded-For` enviado por el visitante. Si se despliega detrás de un proxy, debe configurarse el reconocimiento de direcciones de clientes únicamente para proxies de confianza; varias instancias necesitarían un límite compartido para aplicar una cuota global.

Solo `POST /contacto` es público. Los demás endpoints conservan sus reglas JWT. El interceptor de Angular omite el token para este POST, igual que para login y recuperación.

## Comprobación

`mvn test` comprueba validación, acceso anónimo, CORS, errores SMTP, límite por dirección y concurrencia. El remitente, destinatario y `Reply-To` se comprueban con `JavaMailSender` simulado. También se comprueba que el correo de recuperación conserve su código y duración. Las pruebas no envían correos reales.

La prueba de aceptación debe hacerse desde la landing: enviar una consulta, comprobar su recepción en la bandeja configurada y responder al visitante. Una respuesta HTTP 200 confirma la aceptación por SMTP; no garantiza la entrega en la bandeja de entrada.
