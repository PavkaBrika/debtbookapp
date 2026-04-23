package com.breckneck.debtbook.goal.main.model

import java.io.Serializable

data class GoalUi(
    val goalId: Int,
    val name: String,
    val photoPath: String?,
    val hasPhoto: Boolean,
    val savedAmount: String,
    val totalAmount: String,
    val currency: String,
    val isReached: Boolean,
    val remainingAmount: String?,
    val date: String?,
    val isOverdue: Boolean,
) : Serializable
