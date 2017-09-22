/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.biswajitapps.voiceassistant.ai

object Config {
    // copy this keys from your developer dashboard
    val ACCESS_TOKEN = "11fe88793a1d4539aa870c6cccfcd393"

    val languages = arrayOf(LanguageConfig("en", "a11ea1d839e3446d84e402cb97cdadfb"),
            LanguageConfig("ru", "c8acebfbeeaa42ccb986e30573509055"),
            LanguageConfig("de", "ae2afb2dfd3f4a02bb0da9dd32b78ff6"),
            LanguageConfig("pt", "b27372e24ee44db48df4dccbd57ea021"),
            LanguageConfig("pt-BR", "a4e08b5bc87a41098237e3f23a5e1351"),
            LanguageConfig("es", "49be4c10b6a543dfb41d49d88731bd49"),
            LanguageConfig("fr", "62161233bc094a75b3acfe16aeeed203"),
            LanguageConfig("it", "57f80c9c9a2b4e0eae1739349a46e342"),
            LanguageConfig("ja", "b92617a3f82e4b52b3db44436d2d4b8b"),
            LanguageConfig("ko", "447a595234d74561a76b669a88ab3d99"),
            LanguageConfig("zh-CN", "52d2b2bd992749409fc3a7d0605c3db4"),
            LanguageConfig("zh-HK", "760c7a5efe5d43b9a90d62f73251de6a"),
            LanguageConfig("zh-TW", "9cadea114425436cbaeaa504ea56555b"))

    val events = arrayOf("hello_event", "goodbye_event", "how_are_you_event")
}
