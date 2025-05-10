package abdulrahman.ali19.samplestickerapp.di

import abdulrahman.ali19.samplestickerapp.data.StickerPackLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStickerProvider(): StickerPackLoader {
        return StickerPackLoader()
    }

}