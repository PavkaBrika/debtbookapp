package repository

import com.breckneck.deptbook.data.storage.AdStorage
import com.breckneck.deptbook.domain.repository.AdRepository

class AdRepositoryImpl(val adStorage: AdStorage): AdRepository {

    override fun getClicks(): Int {
        return adStorage.getClicks()
    }

    override fun saveClick(click: Int) {
        adStorage.saveClick(click = click)
    }
}