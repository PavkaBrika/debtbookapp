package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository

class DeleteHumanUseCase(val humanRepository: HumanRepository) {

    fun execute(id: Int) {
        humanRepository.deleteHuman(id)
    }
}