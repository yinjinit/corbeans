/*
 *     Corbeans: Corda integration for Spring Boot
 *     Copyright (C) 2018 Manos Batsis
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 3 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */
package com.github.manosbatsis.corbeans.spring.boot.corda.bnms.web

import com.github.manosbatsis.corbeans.spring.boot.corda.bnms.message.MembershipRequestMessage
import com.github.manosbatsis.corbeans.spring.boot.corda.bnms.web.support.CorbeansBmnsMemberBaseController
import com.r3.businessnetworks.membership.states.MembershipState
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 *  Exposes BNMS member methods as endpoints for a single node or,
 *  by overriding `getRequestNodeName()`, multiple nodes.
 *
 *  To use the controller simply extend it and add a `@RestController` annotation:
 *
 *  ```
 *  @RestController
 *  class MyBmnsMemberController: CorbeansBmnsMemberController()
 *  ```
 *
 *  @see CorbeansBmnsMemberPathFragmentController
 */
@RequestMapping(path = arrayOf("api/bnms/member"))
open class CorbeansBmnsMemberController : CorbeansBmnsMemberBaseController() {

    companion object {
        private val logger = LoggerFactory.getLogger(CorbeansBmnsMemberController::class.java)
    }

    /** Override to control how the the node name is resolved based on the request by e.g. parsing headers */
    open fun getRequestNodeName(): Optional<String> = Optional.empty()


    @PostMapping("memberships")
    @ApiOperation(value = "Request the BNO to kick-off the on-boarding procedure.")
    fun createMembershipRequest(@RequestBody input: MembershipRequestMessage): MembershipState<*> =
            super.createMembershipRequest(getRequestNodeName(), input)


    @PutMapping("memberships")
    @ApiOperation(value = "Propose a change to the membership metadata.")
    fun ammendMembershipRequest(@RequestBody input: MembershipRequestMessage): MembershipState<*> =
            super.ammendMembershipRequest(getRequestNodeName(), input)


    @GetMapping("memberships")
    @ApiOperation(value = "Get a memberships list from a BNO.",
            notes = "Members retrieve the full list on the first invocation only. " +
                    "All subsequent updates are delivered via push notifications from the BNO. " +
                    "Memberships cache can be force-refreshed by setting forceRefresh of GetMembershipsFlow to true. " +
                    "Members that are missing from the Network Map are filtered out from the result list.")
    fun listMemberships(
            @ApiParam(value = "The BNO party name")
            @RequestParam(required = true)
            bno: String,
            @ApiParam(value = "Whether to force a refresh.")
            @RequestParam(required = false, defaultValue = "false")
            forceRefresh: Boolean,
            @ApiParam(value = "Whether to filter out anyone missing from the Network Map.")
            @RequestParam(required = false, defaultValue = "true")
            filterOutMissingFromNetworkMap: Boolean
    ): List<MembershipState<*>> =
            super.listMemberships(getRequestNodeName(), bno, forceRefresh, filterOutMissingFromNetworkMap)



}