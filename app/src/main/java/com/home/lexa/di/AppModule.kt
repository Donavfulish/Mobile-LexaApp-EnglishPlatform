package com.home.lexa.di

import com.home.lexa.core.network.AuthInterceptor
import com.home.lexa.core.network.TokenAuthenticator
import com.home.lexa.data.local.ScheduleTimeManager
import com.home.lexa.data.local.TokenManager
import com.home.lexa.data.local.UserManager
import com.home.lexa.data.remote.AuthApiService
import com.home.lexa.data.remote.CourseApiService
import com.home.lexa.data.remote.ProfileApiService
import com.home.lexa.data.remote.FlashcardApiService
import com.home.lexa.data.remote.DeckApiService
import com.home.lexa.data.remote.ParagraphApiService
import com.home.lexa.data.remote.SpeakingDayApiService
import com.home.lexa.data.repository.AuthRepositoryImpl
import com.home.lexa.data.repository.CourseRepositoryImpl
import com.home.lexa.data.repository.DeckRepositoryImpl
import com.home.lexa.data.repository.FlashcardRepositoryImpl
import com.home.lexa.data.repository.ParagraphRepositoryImpl
import com.home.lexa.data.repository.ProfileRepositoryImpl
import com.home.lexa.data.repository.SpeakingDayRepositoryImpl
import com.home.lexa.domain.repository.AuthRespository
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.domain.repository.FlashcardRepository
import com.home.lexa.domain.repository.ParagraphRepository
import com.home.lexa.domain.repository.ProfileRepository
import com.home.lexa.domain.repository.SpeakingDayRepository
import com.home.lexa.ui.auth.AuthViewModel
import com.home.lexa.ui.course.course_detail.CourseDetailViewModel
import com.home.lexa.ui.course.vocabulary_flashcard.VocabularyFlashcardViewModel
import com.home.lexa.ui.home.HomeViewModel
import com.home.lexa.ui.profile.profile.ProfileViewModel
import com.home.lexa.ui.library.favorite_library.FavoriteLibraryModel
import com.home.lexa.ui.library.personal_library.PersonalLibraryModel
import com.home.lexa.ui.course.student_course_list.StudentCourseListModel
import com.home.lexa.ui.course.teacher_course_list.TeacherCourseListModel
import com.home.lexa.ui.speaking.speaking_practice.SpeakingPracticeViewModel
import com.home.lexa.ui.flashcard.flashcard_edit_add.FlashcardEditAddViewModel
import com.home.lexa.ui.flashcard.exercise_mode.ExerciseModeViewModel
import com.home.lexa.ui.flashcard.exercise_result.ExerciseResultViewModel
import com.home.lexa.ui.profile.profile_notification.ProfileNotificationViewModel
import com.home.lexa.ui.speaking.speaking_practice.SpeakingPracticeStudentViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // 1. Khởi tạo Retrofit (Cốt lõi mạng)
    /*
    single {...}: Tạo singleton -> Tạo ra môt object duy nhất cho toàn bộ app
     */
    single { TokenManager(androidContext()) }
    single { UserManager(androidContext()) }
    single { ScheduleTimeManager(androidContext()) }

    // 2. Khởi tạo Mạng
    single{ AuthInterceptor(get()) }
    single{ TokenAuthenticator(get(), getKoin())}

    single(named("auth_client")) {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .client(get(named("auth_client")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 3. Khởi tạo Api Services
    single { get<Retrofit>().create(CourseApiService::class.java) }
    single { get<Retrofit>().create(FlashcardApiService::class.java) }
    single { get<Retrofit>().create(DeckApiService::class.java) }
    single { get<Retrofit>().create(AuthApiService::class.java) }
    single { get<Retrofit>().create(ProfileApiService::class.java) }
    single { get<Retrofit>().create(ParagraphApiService::class.java) }
    single { get<Retrofit>().create(SpeakingDayApiService::class.java) }


    // 4. Khởi tạo Repositories
    single<CourseRepository> { CourseRepositoryImpl(get()) }
    single<AuthRespository>{ AuthRepositoryImpl(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<DeckRepository>{ DeckRepositoryImpl(get()) }
    single<FlashcardRepository> { FlashcardRepositoryImpl(get()) }
    single<ParagraphRepository> { ParagraphRepositoryImpl(get()) }
    single<SpeakingDayRepository> { SpeakingDayRepositoryImpl(get()) }


    // 5. Khởi tạo ViewModels
    viewModel { HomeViewModel(get<CourseRepository>(), get()) }
    viewModel { AuthViewModel(androidApplication(), get(), get(), get(),get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { FavoriteLibraryModel(get()) }
    viewModel { PersonalLibraryModel(get()) }
    viewModel { StudentCourseListModel (get()) }
    viewModel { TeacherCourseListModel(get()) }
    viewModel { CourseDetailViewModel(get(), get(), get(), get(), get())}
    viewModel { VocabularyFlashcardViewModel(get(), get())}
    viewModel { ExerciseModeViewModel(get())}
    viewModel { ExerciseResultViewModel(get(), get())}
    viewModel { FlashcardEditAddViewModel(get(), get())}
    viewModel { SpeakingPracticeViewModel(get(), get()) }
    viewModel { ProfileNotificationViewModel(get()) }
    viewModel { SpeakingPracticeStudentViewModel(get(), get()) }
}
