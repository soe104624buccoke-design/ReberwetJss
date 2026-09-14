# Production configuration

Required Firebase/Android values are intentionally not embedded in source.

Required:
- Firebase project ID
- Android package: ke.reberwet.jss.portal
- app/google-services.json
- Firebase Auth enabled
- Firestore enabled
- Storage enabled
- App Check enabled for release
- FCM configured if push notifications are desired

Signing:
- REBERWET_KEYSTORE_PASSWORD
- REBERWET_KEY_ALIAS
- REBERWET_KEY_PASSWORD
- REBERWET_KEYSTORE_PATH

Never place service-account private keys in the Android app.
