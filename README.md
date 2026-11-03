# Лаб 8: Mockito-оор mock объект ашиглан тестлэх

**Оюутан:** Г. Лувсанжал, **код:** B242270016
**GitHub:** jal0115

## Орчин

    java version "23.0.1" 2024-10-15
    Java(TM) SE Runtime Environment (build 23.0.1+11-39)
    Java HotSpot(TM) 64-Bit Server VM (build 23.0.1+11-39, mixed mode, sharing)
    [1mApache Maven 3.9.9[m
    Maven home: /usr/share/maven
    Java version: 23.0.1, vendor: Oracle Corporation, runtime: /usr/lib/jvm/jdk-23
    Default locale: en, platform encoding: UTF-8
    OS name: "linux", version: "6.12.107+deb13-amd64", arch: "amd64", family: "unix"

## Тестийн тоо

`results/mvn-test.txt`-ийн сүүлийн мөр: `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0`, `BUILD SUCCESS`.
7 энгийн тест (A1-A7) + A8-ийн 3 мөр = 10.

## A8: lenient()

A8-д мөр бүр өөр stub тавьдаг. `lenient()`-ийг түр авч шалгахад энэ загварт 10 тест ногоон хэвээр байсан, учир нь мөр бүрийн тавьсан stub бүгд ашиглагддаг. Гэхдээ нэг параметржүүлсэн тестэд мөр бүр өөр нөхцөлөөр stub тавьдаг тул, зарим мөр stub-аа ашиглахгүй болбол (код эрт буцвал эсвэл мутаци орвол) MockitoExtension-ий strict stubs `UnnecessaryStubbingException` өгөхөөс сэргийлж `lenient()`-ийг үлдээсэн.

## Хэсэг B: мутаци

| Мутант | Эвдрэл | Унасан тестүүд | Failures / Errors | Файл |
|---|---|---|---|---|
| 1 | идэвхгүй оюутны `if (!student.active())`-ийг комментлосон | `register_inactiveStudent_returnsErrorInactiveAndNoEmailInteraction` (A3), `register_errorScenarios_returnExpectedErrorCode[2]` (A8, INACTIVE мөр) | 2 / 0 | results/mvn-test-mutant-1.txt |
| 2 | `saveRegistration(...)` дуудалтыг комментлосон | `register_activeStudentWithPrereqs_returnsOkSavesAndSendsEmail` (A1), `register_emailSenderThrows_stillReturnsOkAndSaves` (A5) | 2 / 0 | results/mvn-test-mutant-2.txt |
| 3 | `courseId == null` шалгалтыг устгасан | `register_nullArgument_throwsIllegalArgumentAndTouchesNoDependency` (A6, засварын дараа) | 1 / 0 | results/mvn-test-mutant-3.txt |

**Мутант 3:** A6 эхэндээ зөвхөн `studentId = null`-ийг шалгаж байсан тул мутант амьд үлдсэн (`results/mvn-test-mutant-3-before-fix.txt`: Failures: 0, BUILD SUCCESS). A6-д `courseId = null`-ийн шалгалтыг нэмсний дараа мутантыг барьсан. Мутант 1 ба 2 нь `Failures` (assertion/verify унасан), `Errors` биш.

Мутаци бүрийн дараа эх кодыг `git checkout -- src/main`-аар сэргээсэн. Эцсийн `results/mvn-test.txt` нь эх кодтой ногоон гаралт.

## Дүгнэлт

Энэ лабт `StudentRepository`, `EmailSender` хоёрт бодит хэрэгжүүлэлт байхгүй байсан тул Mockito-оор тэдгээрийг орлуулж, `register()`-ийн логикийг бусдаас тусгаарлан тестэлсэн. Mock нь хариултыг `when(...).thenReturn(...)`-ээр зааж өгч, хийгдсэн дуудалтыг `verify`-аар шалгадаг бол гараар бичсэн fake нь жинхэнэ хэрэгжүүлэлтийг хялбарчилсан (жишээ нь санах ойн `Map`-тай repository) өөрийн төлөвтэй класс юм. Fake олон тестэд дахин ашиглагддаг ч өөрөө бичиж засах хэрэгтэй, харин mock нь тест бүрийн нөхцөлд хурдан тохируулагддаг. Mock хийх ёсгүй зүйл нь `Student` record шиг энгийн өгөгдлийн объект, цэвэр логик, мөн тестлэж буй классыг өөрийг нь, учир нь тэдгээрийг mock-доход бодит логик шалгагдахгүй болно. `verify`-г хэтрүүлбэл тест кодын дотоод дуудалтын дараалалд хэт баригдаж, зан төлөв нь хэвээр байхад refactor хийхэд л унадаг эмзэг тест болно, тиймээс `never()`, `verifyNoInteractions`-ийг зөвхөн чухал хориглолтод (жишээ нь алдаа гарвал и-мэйл явуулахгүй) хэрэглэсэн нь зөв гэж үзсэн. Мутацийн шалгалтаар мутант 1-д A3 ба A8-ийн INACTIVE мөр, мутант 2-д A1 ба A5 унасан нь эдгээр тестүүд үнэхээр ажил хийж байгааг харуулсан. Харин мутант 3 эхэндээ амьд үлдсэн нь тест бүгд ногоон байсан ч A6 зөвхөн `studentId = null`-ийг шалгаж байсан цоорхойг илрүүлсэн бөгөөд хоёр аргументыг тус тусад нь шалгадаг болгосны дараа мутант баригдсан. Үүнээс би "ногоон тест" нь "сайн тест" гэсэн үг биш, тестээ эвдэж үзэж л чанарыг нь баталгаажуулдаг гэдгийг ойлгосон.
