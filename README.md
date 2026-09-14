# ReberwetJS — Reberwet Junior Secondary School Portal

Production-oriented Android project scaffold for:
`ke.reberwet.jss.portal`

## Stack
- Kotlin + Jetpack Compose
- Android API 36
- Firebase Authentication
- Cloud Firestore
- Cloud Storage
- Firebase App Check
- Firebase Cloud Messaging
- Android BiometricPrompt
- PDF/CSV export dependencies

## Current state
This repository is deliberately split into:
1. A functional DEMO/TEST mode that can be run without real school data.
2. Production backend integration points that require the school's Firebase project.

The demo learner list uses fictional DEMO learners and must not be mixed with production data.

## Firebase setup
1. Create a Firebase project.
2. Add Android app with package:
   `ke.reberwet.jss.portal`
3. Download `google-services.json`.
4. Place it at:
   `app/google-services.json`
5. Enable Email/Password authentication and Password Reset.
6. Enable Firestore.
7. Enable Storage.
8. Enable App Check with Play Integrity for release.
9. Configure Firebase Cloud Messaging if push notifications are required.
10. Create an administrator account and assign the admin role.

For a real deployment, add the Google Services Gradle plugin and its version compatible with the project's Android Gradle Plugin, then apply:
`com.google.gms.google-services`
to the app module.

## Firestore schema
Top-level collections:
- users
- learners
- teachers
- grades
- classes
- subjects
- marks
- rubric_levels
- terms
- academic_years
- report_cards
- audit_logs
- notifications
- resources
- school_settings

Recommended key fields:
### users/{uid}
role, displayName, active, teacherId, classIds, subjectIds, createdAt

### learners/{learnerId}
admissionNumber, fullName, gender, guardianName, guardianPhone, active

### learner_enrolments/{enrolmentId}
learnerId, academicYearId, gradeId, classId, status

### marks/{markId}
learnerId, subjectId, termId, academicYearId, marks, rubricLevel, rubricPoints, createdBy, updatedAt

### report_cards/{reportCardId}
learnerId, termId, academicYearId, totalRubricPoints, overallGrade, generalComment, generatedAt

## Promotion model
Never overwrite historical enrolment records.
Create a new enrolment for the new academic year:
Grade 7 -> Grade 8 -> Grade 9 -> Completed.
Admission number and permanent learner ID remain stable.

## Initial subjects
Mathematics
English
Kiswahili
Creative Arts & Sports
Agriculture & Nutrition
Social Studies
Christian Religious Education
Pre-Technical Studies
Integrated Science

## Initial class teachers
Grade 9 — Mr. Bore N.
Grade 8 — Madam Faith Chepkirui
Grade 7 — Madam Nelly Korir

## Rubric defaults
EE1=8, EE2=7
ME1=6, ME2=5
AE1=4, AE2=3
BE1=2, BE2=1

Grade ranges:
EE 70-100
ME 45-69
AE 15-44
BE 0-14

Maximum report-card rubric score:
9 subjects × 8 = 72.

Actual marks must remain hidden from the generated report card, while authorized administrators can use them for internal analysis.

## Security
Use Firebase Security Rules so:
- unauthenticated users have no learner access;
- teachers can only access assigned subjects/classes;
- class teachers can access their assigned class;
- administrators can manage configuration;
- destructive actions require confirmation and should normally deactivate records;
- audit_logs are append-only for ordinary users.

Do not put service-account credentials inside the APK.

## Release signing
Create a private upload keystore outside source control:
`keytool -genkeypair -v -keystore reberwet-upload.jks -keyalg RSA -keysize 4096 -validity 10000 -alias reberwet`

Never commit the keystore or passwords to Git.

Configure release signing in `app/build.gradle.kts` using environment variables or `keystore.properties`.

## Build
Debug APK:
`./gradlew assembleDebug`

Release APK:
`./gradlew assembleRelease`

Play Store bundle:
`./gradlew bundleRelease`

The resulting AAB is under:
`app/build/outputs/bundle/release/`

## Play Store checklist
- Create Google Play Console developer account.
- Reserve `ReberwetJS` app listing.
- Upload signed AAB.
- Complete Data Safety form.
- Provide privacy policy URL.
- Add app icon, screenshots and feature graphic.
- Declare only required permissions.
- Complete content rating.
- Complete target audience declaration.
- Test internal/closed release before production.
- Verify Firebase rules with Emulator Suite.
- Do not upload real learner data as demo content.

## Remaining configuration
- School logo asset.
- Actual learner images/lists.
- Firebase project + google-services.json.
- Administrator account.
- Final school grading/rubric configuration.
- Privacy policy URL.
- Release signing key.
- Google Play Console account.
- Production notification configuration.

## Important
This package is a real Android Studio project, but it is not honest to call the backend-connected release "fully production-ready" until the school's Firebase project and release credentials are configured and the release build is tested against that backend.
